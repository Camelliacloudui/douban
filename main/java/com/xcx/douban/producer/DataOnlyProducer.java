package com.xcx.douban.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xcx.douban.crawler.Content;
import com.xcx.douban.crawler.DoubanCrawler;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class DataOnlyProducer {

    // 增加关闭标记，用于优雅终止while循环
    private static volatile boolean isRunning = true;
    private static KafkaProducer<String, String> producer;

    public static void main(String[] args) throws Exception {
        // 1. 爬取数据
        DoubanCrawler crawler = new DoubanCrawler();
        ObjectMapper mapper = new ObjectMapper();

        // 2. 创建 Kafka 生产者（原生，不依赖 Spring）
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producer = new KafkaProducer<>(props);

        // JVM钩子：程序退出时关闭producer，释放资源（解决第一个try-with-resources警告）
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            isRunning = false;
            producer.flush();
            producer.close();
            System.out.println("Kafka生产者已正常关闭");
        }));

        // 死循环：持续爬取发送，解决水位线eventTime过长而数据只有一次发送无法存入数据库的问题
        while (isRunning) {
            try {
                // 1.爬取数据
                List<Content> list = crawler.getData();

                // 2.逐条发送kafka
                for (Content c : list) {
                    String json = mapper.writeValueAsString(c);
                    producer.send(new ProducerRecord<>("douban-movie-topic", json));
                }
                System.out.println("本轮发送完成，共 " + list.size() + " 条");
            } catch (Exception e) {
                System.err.println("本轮爬取/发送异常：" + e.getMessage());
                // 替换e.printStackTrace()，改用标准错误输出
                System.err.printf("异常详情：%s%n", e);
            }

            // TimeUnit.sleep替代Thread.sleep，规避忙等待警告
            TimeUnit.SECONDS.sleep(60);
        }
    }
}
