package com.xq.fin.analyser.config;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * 解决数据库没有创建的情况
 *
 * @author sk-qianxiao
 * @date 2020/4/16
 */
@Configuration
@Primary
@Slf4j
public class DataSourceConfig {
    @Value("${spring.datasource.url}")
    private String datasourceUrl;
    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;

    @Bean
    public DataSource dataSource() {
        DruidDataSource datasource = new DruidDataSource();

        datasource.setUrl(datasourceUrl);
        datasource.setUsername(username);
        datasource.setPassword(password);
        datasource.setDriverClassName(driverClassName);
        try {
            Class.forName(driverClassName);
            String url01 = datasourceUrl.substring(0, datasourceUrl.indexOf("?"));
            String url02 = datasourceUrl.substring(datasourceUrl.indexOf("?"));
            String datasourceName = url01.substring(url01.lastIndexOf("/") + 1);
            url01 = url01.substring(0, url01.lastIndexOf("/") + 1);

            // 连接已经存在的数据库，如：mysql
            Connection connection = DriverManager.getConnection(url01 + url02, username, password);
            Statement statement = connection.createStatement();


            //判断数据库是否存在
            boolean databaseExist = false;
            ResultSet rs = statement.executeQuery("show databases");
            while (rs.next()) {
                String name = rs.getString(1);
                if (datasourceName.equals(name)) {
                    databaseExist = true;
                    break;
                }
            }

            /** 如果数据库不存在就创建数据库*/
            if (!databaseExist) {
                statement.executeUpdate("CREATE DATABASE " + datasourceName+" default CHARACTER SET utf8 collate utf8_general_ci");
            }

            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


        return datasource;
    }
}
