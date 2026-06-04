package com.xcx.douban.flink.function;

import com.xcx.douban.flink.HotMovie;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.java.tuple.Tuple4;

public class HotMovieAggregateFunction implements AggregateFunction<HotMovie, Tuple4<String, Double, Integer, String>, HotMovie> {

    @Override
    public Tuple4<String, Double, Integer, String> createAccumulator() {
        return Tuple4.of("", 0.0, 0, "");
    }

    @Override
    public Tuple4<String, Double, Integer, String> add(HotMovie value, Tuple4<String, Double, Integer, String> acc) {
        String title = acc.f0.isEmpty() ? value.getTitle() : acc.f0;
        String pText = acc.f3.isEmpty() ? value.getPeopleText() : acc.f3;
        double score = acc.f0.isEmpty() ? value.getScoreNumber() : acc.f1;
        return Tuple4.of(title, score, acc.f2 + 1, pText);
    }

    @Override
    public HotMovie getResult(Tuple4<String, Double, Integer, String> acc) {
        HotMovie result = new HotMovie();
        result.setTitle(acc.f0);
        result.setAvgScore(acc.f1 );
        // 关键：回填文本
        result.setPeopleText(acc.f3);
        return result;
    }

    @Override
    public Tuple4<String, Double, Integer, String> merge(Tuple4<String, Double, Integer, String> a, Tuple4<String, Double, Integer, String> b) {
        String title = a.f0.isEmpty() ? b.f0 : a.f0;
        String pText = a.f3.isEmpty() ? b.f3 : a.f3;
        double score = a.f0.isEmpty() ? b.f1 : a.f1;
        return Tuple4.of(title, score, a.f2 + b.f2, pText);
    }
}