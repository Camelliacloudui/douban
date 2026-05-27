package com.xcx.douban.web.mapper;

import com.xcx.douban.commen.Movie;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MovieMapper {
    @Insert("INSERT INTO movie_top250(title, score, people_text) VALUES(#{title}, #{score}, #{people})")
    void insert(Movie movie);


}
