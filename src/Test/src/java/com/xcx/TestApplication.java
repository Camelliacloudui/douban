package com.xcx;

import com.xcx.douban.web.controller.MovieController;
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

        // 从容器中获取 Controller
        MovieController controller = context.getBean(MovieController.class);

        // 调用接口方法，测试爬虫+保存数据
        controller.saveMovies();

    }
}
