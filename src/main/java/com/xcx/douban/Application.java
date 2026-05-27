package com.xcx.douban;

import com.xcx.douban.web.service.MovieService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;

@SpringBootApplication
public class Application {
    public static void main(String[] args) throws Exception {
        // 启动 Spring Boot 容器
        ApplicationContext context = SpringApplication.run(Application.class, args);

        // 从容器中获取 Service
        MovieService movieService = context.getBean(MovieService.class);

        // 执行爬虫 + 存数据库 + 发 Kafka
        movieService.crawlAndSaveToDB();


    }

}
