package com.xcx.douban.web.service.impl;

import com.xcx.douban.commen.Movie;
import com.xcx.douban.crawler.Content;
import com.xcx.douban.crawler.DoubanCrawler;
import com.xcx.douban.web.mapper.MovieMapper;
import com.xcx.douban.web.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class MovieServiceImpl implements MovieService {
    @Autowired
    private MovieMapper movieMapper;

    @Override
    public void saveMoviesFromCrawler() {
        DoubanCrawler doubanCrawler = new DoubanCrawler();

        try {
            List<Content> list = doubanCrawler.getData();

            for(Content c:list){
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
                movieMapper.insert(movie);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
