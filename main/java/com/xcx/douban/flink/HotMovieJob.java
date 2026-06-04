package com.xcx.douban.flink;

import com.xcx.douban.flink.function.HotMovieAggregateFunction;
import com.xcx.douban.flink.function.JsonToMovieMapFunction;
import com.xcx.douban.flink.sink.MySQLSink;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;

import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;

import java.time.Duration;
import java.util.Objects;


public class HotMovieJob {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // Checkpoint（保留）
        env.enableCheckpointing(5000);
        env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        env.getCheckpointConfig().setCheckpointStorage("file:///tmp/flink-checkpoints");

        // Kafka Source
        KafkaSource<String> source = KafkaSource.<String>builder()
                .setBootstrapServers("localhost:9092")
                .setTopics("douban-movie-topic")
                .setGroupId("flink-group")
                .setStartingOffsets(OffsetsInitializer.committedOffsets(OffsetResetStrategy.EARLIEST))
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();

        // 读取原始数据（String）
        DataStream<String> rawStream = env.fromSource(
                source,
                WatermarkStrategy.noWatermarks(),
                "Kafka Source"
        );

        // 解析 + 水位线
        DataStream<HotMovie> movieStream = rawStream
                .map(new JsonToMovieMapFunction())
                .filter(Objects::nonNull)
                .assignTimestampsAndWatermarks(
                        WatermarkStrategy.<HotMovie>forBoundedOutOfOrderness(Duration.ofSeconds(5))
                                .withTimestampAssigner((movie, ts) -> movie.getTimestamp())
                                // 它的意思是：如果一个数据源10秒内都没新消息，就标记它为"空闲"
                                // 这样水印就不会被这个空闲的分片卡住了
                                .withIdleness(Duration.ofSeconds(10))
                );

        // 窗口聚合
        DataStream<HotMovie> windowStream = movieStream
                .keyBy(HotMovie::getTitle)
                .window(TumblingEventTimeWindows.of(Time.seconds(60)))
                .aggregate(new HotMovieAggregateFunction());

        // 写入 MySQL
        windowStream.addSink(new MySQLSink());

        env.execute("豆瓣电影实时热度分析");

    }
}