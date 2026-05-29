package com.xcx.douban.flink.function;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xcx.douban.flink.Movie;
import com.xcx.douban.utils.JsonUtils;
import org.apache.flink.api.common.functions.MapFunction;

/**
 * 自定义MapFunction：JSON字符串 → Movie对象
 * 封装解析+热度计算，主Job更干净
 */
public class JsonToMovieMapFunction implements MapFunction<String, Movie> {
    public JsonToMovieMapFunction() {}
    private transient ObjectMapper mapper;
    @Override
    public Movie map(String jsonStr) throws Exception {
        System.out.println("原始 JSON: " + jsonStr);

        Movie temp = JsonUtils.toBean(jsonStr, Movie.class);
        if (temp == null) {
            return null;
        }

        // 脏数据过滤（按真实字段）
        if (temp.getTitle() == null || temp.getTitle().isEmpty()
                || temp.getScore() == null || temp.getScore().isEmpty()
                || temp.getPeopleText() == null || temp.getPeopleText().isEmpty()) {
            return null;
        }
        // 直接返回，热度分在 Sink 中计算
        return temp;
    }
}
