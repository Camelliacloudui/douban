package com.xcx.douban.flink.function;

import com.xcx.douban.flink.HotMovie;
import com.xcx.douban.utils.JsonUtils;
import org.apache.flink.api.common.functions.MapFunction;

/**
 * 自定义MapFunction：JSON字符串 → Movie对象
 * 封装解析+热度计算，主Job更干净
 */
public class JsonToMovieMapFunction implements MapFunction<String, HotMovie> {
    public JsonToMovieMapFunction() {}

    @Override
    public HotMovie map(String jsonStr) {
        System.out.println("原始 JSON: " + jsonStr);

        HotMovie temp = JsonUtils.toBean(jsonStr, HotMovie.class);
        if (temp != null) {
            System.out.println("解析后 timestamp = " + temp.getTimestamp());
        }

        if (temp != null && (temp.getTitle() == null || temp.getTitle().isEmpty())) {
            System.err.println("标题为空，返回 null");
            return null;
        }

        // 直接返回，热度分在 Sink 中计算
        return temp;
    }
}
