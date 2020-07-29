package com.mindata.blockchain.core.sqlite.config;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;


/**
 * 配置sqlite数据库的DataSource
 * @author wuweifeng wrote on 2018/3/2.
 */
@Configuration
public class DataSourceConfiguration {

    @Value("${jdbc.url}")
    String url;

    @Value("${jdbc.username}")
    String username;
    @Value("${jdbc.password}")
    String password;
    @Bean(destroyMethod = "", name = "EmbeddeddataSource")
    public DataSource dataSource() {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName("com.mysql.cj.jdbc.Driver");
        dataSourceBuilder.username(username);
        dataSourceBuilder.password(password);
        dataSourceBuilder.url(url);
        dataSourceBuilder.type(MysqlDataSource.class);
        return dataSourceBuilder.build();
    }

}
