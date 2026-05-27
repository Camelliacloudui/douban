package com.xcx.douban.web.service.impl;

import com.xcx.douban.commen.Movie;
import com.xcx.douban.crawler.Content;
import com.xcx.douban.crawler.DoubanCrawler;
import com.xcx.douban.kafka.KafkaProducer;
import com.xcx.douban.web.mapper.MovieMapper;
import com.xcx.douban.web.service.MovieService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class MovieServiceImpl implements MovieService {
    private final MovieMapper movieMapper;
    private final KafkaProducer kafkaProducer;
    public MovieServiceImpl(MovieMapper movieMapper,KafkaProducer kafkaProducer) {
        this.movieMapper = movieMapper;
        this.kafkaProducer = kafkaProducer;
    }

    private List<Movie> cachedMovies = new ArrayList<>();

    @Override
    public List<Movie> saveMoviesFromCrawler() {
        DoubanCrawler doubanCrawler = new DoubanCrawler();
        List<Movie> result = new ArrayList<>();

        try {
            List<Content> list = doubanCrawler.getData();

            list.forEach(c ->{
                Movie movie = new Movie();

                //判空处理
                if (c.getTitle()!= null && !c.getTitle().isEmpty()) {
                    movie.setTitle(c.getTitle());
                }else{
                    movie.setTitle(null);
                }

                // 把String类型的score转换成Double
                if (c.getScore() != null && !c.getScore().isEmpty()) {
                    movie.setScore(Double.parseDouble(c.getScore()));
                } else {
                    movie.setScore(0.0); // 处理空值
                }

                // 把String类型的peopleText转换成Integer（先去掉非数字字符）
                if (c.getPeopleText() != null && !c.getPeopleText().isEmpty()) {
                    // 提取数字部分
                    String peopleStr = c.getPeopleText().replaceAll("\\D", "");
                    if (!peopleStr.isEmpty()) {
                        movie.setPeople(Integer.parseInt(peopleStr));
                    } else {
                        movie.setPeople(0);
                    }
                } else {
                    movie.setPeople(0);
                }

                result.add(movie);
            });
            this.cachedMovies = result;
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Movie> getMoviesFromCrawler(){
        return this.cachedMovies;
    }

    @Override
    public void saveMoviesToDB(List<Movie> movies){
        movies.forEach (movie -> {
            movieMapper.insert(movie);
            //后续为了应对高并发会使用批量发送接口
            kafkaProducer.send(movie);
        });
    }

    //确保存储成功，避免重复或半成功
    @Override
    @Transactional
    public void crawlAndSaveToDB(){
        List<Movie> movies = saveMoviesFromCrawler();
        saveMoviesToDB(movies);
    }
}
