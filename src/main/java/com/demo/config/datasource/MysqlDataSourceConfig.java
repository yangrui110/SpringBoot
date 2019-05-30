package com.demo.config.datasource;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @autor 杨瑞
 * @date 2019/5/17 8:35
 */
@Data
@Component
@ConfigurationProperties(prefix = "spring.datasource.mysql")
public class MysqlDataSourceConfig {

    private String username;
    private String password;
    private String url;

    @Override
    public String toString() {
        return "MysqlDataSourceConfig{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
