package com.xcx.douban.web.service;


import com.xcx.douban.commen.Movie;

import java.util.List;

public interface MovieService {
    List<Movie> saveMoviesFromCrawler();

    List<Movie> getMoviesFromCrawler();

    void saveMoviesToDB(List<Movie> movies);

    void crawlAndSaveToDB();
}
