package com.demo.config.datasource.dynamic;

import com.alibaba.druid.pool.DruidDataSource;

import java.sql.SQLException;

/**
 * @autor 杨瑞
 * @date 2019/6/13 22:09
 */
public class DefaultDruidDataSourceBuilder {

    public static DruidDataSource makeDefaultDruiDataSource(){
        DruidDataSource druidDataSource=new DruidDataSource();
        druidDataSource.setMaxActive(10);
        druidDataSource.setMinIdle(5);
        druidDataSource.setMaxWait(60000);
        druidDataSource.setTestOnBorrow(false);
        druidDataSource.setTestWhileIdle(true);
        druidDataSource.setTestOnReturn(false);
        druidDataSource.setValidationQuery("select 1");
        druidDataSource.setTimeBetweenEvictionRunsMillis(60000);
        druidDataSource.setMinEvictableIdleTimeMillis(3000);
        druidDataSource.setPoolPreparedStatements(true);
        druidDataSource.setMaxPoolPreparedStatementPerConnectionSize(20);
        try {
            druidDataSource.setFilters("stat,wall,Log4j");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return druidDataSource;
    }
}
