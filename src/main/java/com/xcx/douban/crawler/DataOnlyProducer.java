package com.xcx.douban.crawler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.List;
import java.util.Properties;

public class DataOnlyProducer {
    public static void main(String[] args) throws Exception {
        // 1. 爬取数据
        DoubanCrawler crawler = new DoubanCrawler();
        List<Content> list = crawler.getData();

        // 2. 创建 Kafka 生产者（原生，不依赖 Spring）
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        KafkaProducer<String, String> producer = new KafkaProducer<>(props);

        // 3. 发送数据
        ObjectMapper mapper = new ObjectMapper();
        for (Content c : list) {
            String json = mapper.writeValueAsString(c);
            producer.send(new ProducerRecord<>("douban-movie-topic", json));
        }

        producer.close();
        System.out.println("发送完成，共 " + list.size() + " 条");
        System.exit(0);
    }
}
