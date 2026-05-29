package com.xcx.douban.flink.sink;

import com.xcx.douban.flink.Movie;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class MySQLSink extends RichSinkFunction<Movie> {
    private Connection connection;
    private PreparedStatement preparedStatement;

    @Override
    public void open(Configuration parameters) throws Exception {
        // 1. 加载驱动
        Class.forName("com.mysql.cj.jdbc.Driver");
        // 2. 建立连接（修改成你的密码）
        connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/douban?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true",
                "root",
                "123456"
        );
        // 3. 预编译 SQL
        preparedStatement = connection.prepareStatement(
                "INSERT INTO hot_movie (title, hot_score) VALUES (?, ?)"
        );
    }

    @Override
    public void invoke(Movie movie, Context context) throws Exception {
        // 4. 计算热度分
        double hotScore = movie.getScoreNumber() * 1000 + movie.getPeopleNumber() / 100.0;
        preparedStatement.setString(1, movie.getTitle());
        preparedStatement.setDouble(2, hotScore);
        preparedStatement.executeUpdate();
    }

    @Override
    public void close() throws Exception {
        if (preparedStatement != null) preparedStatement.close();
        if (connection != null) connection.close();
    }
}