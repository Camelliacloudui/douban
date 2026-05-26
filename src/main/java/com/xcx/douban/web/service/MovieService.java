package com.xcx.douban.web.service;


import com.xcx.douban.commen.Movie;

public interface MovieService {
    void saveMoviesFromCrawler();

    Movie getMoviesFromCrawler();
}
