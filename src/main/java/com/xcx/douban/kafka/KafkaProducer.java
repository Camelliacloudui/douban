package com.xcx.douban.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("kafka")
public class KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    private static final String MOVIE = "douban-movie-topic";

    @RequestMapping("/send")
    @ResponseBody
    public String send(String movieJSON) {
        // #2. 发送Kafka消息
        kafkaTemplate.send(MOVIE, movieJSON);
        return "success";
    }
}
