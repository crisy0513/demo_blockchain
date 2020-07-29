package com.mindata.blockchain.core.sqlite.config;

import com.mysql.cj.jdbc.MysqlDataSource;
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

    @Bean(destroyMethod = "", name = "EmbeddeddataSource")
    public DataSource dataSource() {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName("com.mysql.cj.jdbc.Driver");
        dataSourceBuilder.username("greatwall");
        dataSourceBuilder.password("greatwall");
        dataSourceBuilder.url("jdbc:mysql://10.168.1.186:3306/blockchain_4?useUnicode=true&autoReconnect=true&useSSL=false&characterEncoding=UTF-8&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=GMT%2b8");
        dataSourceBuilder.type(MysqlDataSource.class);
        return dataSourceBuilder.build();
    }

}
