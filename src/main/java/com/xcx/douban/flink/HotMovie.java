package com.xcx.douban.flink;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class HotMovie {

    // 使用private+getter的形式来适应解析JSON时的操作，并引入Lombok依赖的Getter减少冗余代码
    private String title;
    private String score;
    private String peopleText;

    private double avgScore;
    private double hotScore;
    private Long timestamp;      // 时间戳
    private Long windowEnd;      // 窗口结束时间

    // 无参构造（Flink 序列化需要）
    public HotMovie() {}

    public HotMovie(String title, double score, int people) {
        this.title = title;
        this.score = String.valueOf(score);
        this.peopleText = people + "人评价";
        //计算热度值
        this.hotScore = score * 1000 + people / 100.0;
    }



    public double getScoreNumber() {
        try {
            return Double.parseDouble(score);
        } catch (Exception e) {
            return 0.0;
        }
    }

    public int getPeopleNumber() {
        if (peopleText == null) return 0;
        String num = peopleText.replaceAll("\\D", "");
        try {
            return Integer.parseInt(num);
        } catch (Exception e) {
            return 0;
        }
    }

    // 更改显示形式而不是乱码
    @Override
    public String toString() {
        return String.format("Movie{title='%s', hotScore=%.2f}", title, hotScore);
    }
}
