package com.xcx.douban.crawler;

import org.jsoup.Jsoup;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.swing.text.AbstractDocument;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;

public class DoubanCrawler {

    public List<Content> getData() throws IOException {
        ArrayList<Content> list = new ArrayList<>();
        for(int start = 0;start <= 225;start +=25) {
            String url = "https://movie.douban.com/top250?start=" + start + "&filter=";

            //模拟服务器避免拒绝访问
            Document document = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(30000)
                    .get();

            Element element = document.getElementById("content");
            Elements elements = element.getElementsByTag("li");

            for (Element el : elements) {
                //增加判空处理
                Elements eltitles = el.select("span.title");
                String title = eltitles.first() != null ? eltitles.first().text() : "";

                Elements elscores = el.select("span.rating_num");
                String score = elscores.first() != null ? elscores.first().text() : "";

                // 评分人数是父 div 里的最后一个 span
                Element peopleEl = el.selectFirst("span:contains(人评价)");
                String peopleText = peopleEl != null ? peopleEl.text() : "";

                Content content = new Content();

                content.setTitle(title);
                content.setScore(score);
                content.setPeopleText(peopleText);
                list.add(content);
            }

        }
        return list;
    }

}
