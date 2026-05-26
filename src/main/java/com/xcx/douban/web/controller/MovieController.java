package com.xcx.douban.web.controller;

import com.xcx.douban.web.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/movie")
public class MovieController {
    @Autowired
    private MovieService movieService;

    @GetMapping("/save")
    public String saveMovies() {
        movieService.saveMoviesFromCrawler();
        return "数据保存成功！";
    }
}
