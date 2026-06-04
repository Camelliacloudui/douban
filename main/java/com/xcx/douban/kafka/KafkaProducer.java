package com.xcx.douban.kafka;

import com.xcx.douban.commen.Movie;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import com.xcx.douban.utils.JsonUtils;

import java.util.List;
/**
 * Kafka 消息生产者
 * 未来可扩展：批量发送、异步回调、发送失败重试
 */
@Service
public class KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    private static final String TOPIC = "douban-movie-topic";

    public void send(Movie movie) {
        kafkaTemplate.send(TOPIC, JsonUtils.toJson(movie));
    }

    //批量发送接口
    public void sendMovies(List<Movie> movies) {
        movies.forEach(movie ->
                kafkaTemplate.send(TOPIC, JsonUtils.toJson(movie))
        );
    }
}
