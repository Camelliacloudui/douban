package com.xcx.douban.commen;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Movie {
    private Integer id;
    private String title;
    private Double score;
    private Integer people;

    private Long timestamp;

}
