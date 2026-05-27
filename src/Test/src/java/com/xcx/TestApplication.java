package com.xcx;

import com.xcx.douban.web.controller.MovieController;
import com.xcx.douban.web.service.MovieService;
import com.xcx.douban.web.service.impl.MovieServiceImpl;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class TestApplication {

    public static void main(String[] args) {
        // 启动 Spring 容器
        ApplicationContext context = SpringApplication.run(TestApplication.class, args);

    }

}
