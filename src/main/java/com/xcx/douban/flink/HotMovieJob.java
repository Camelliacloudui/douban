package com.xcx.douban.flink;

import com.xcx.douban.flink.function.JsonToMovieMapFunction;
import com.xcx.douban.flink.sink.MySQLSink;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;

import java.util.Objects;
import java.util.Properties;


public class HotMovieJob {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // Checkpoint 配置（保证精确一次消费）
        env.enableCheckpointing(5000);
        env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        env.getCheckpointConfig().setCheckpointStorage("file:///tmp/flink-checkpoints");

        // Kafka Source（无界流，不加 setBounded）
        KafkaSource<String> source = KafkaSource.<String>builder()
                .setBootstrapServers("localhost:9092")
                .setTopics("douban-movie-topic")
                .setGroupId("flink-group")
                .setStartingOffsets(OffsetsInitializer.committedOffsets(OffsetResetStrategy.EARLIEST))
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();

        DataStream<String> kafkaStream = env.fromSource(source, WatermarkStrategy.noWatermarks(), "Kafka Source");

        DataStream<Movie> movieStream = kafkaStream
                .map(new JsonToMovieMapFunction())
                .filter(Objects::nonNull);   // 建议保留，防止脏数据

        movieStream.addSink(new MySQLSink());

        env.execute("豆瓣电影实时热度分析");
    }
}