package com.xcx.douban.kafka;

import com.xcx.douban.commen.Movie;
import com.xcx.douban.utils.JsonUtils;
import com.xcx.douban.web.service.MovieService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class KafkaConsumer {
    private final MovieService movieService;
    public KafkaConsumer(MovieService movieService){
        this.movieService = movieService;
    }

    @KafkaListener(topics = "douban-movie-topic", groupId = "douban-group")
    public void listen(String message){
        Movie movie = JsonUtils.toBean(message,Movie.class);
        //由于转换格式时已经判过异常，所以省去try-catch，但是toBean会有返回空置的风险所以增加判空处理
        if (movie == null) {
            System.err.println("消息格式错误，跳过：" + message);
            return;
        }
        movieService.saveMoviesToDB(Collections.singletonList(movie));
    }
}
