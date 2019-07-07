package com.demo.web.core.xmlEntity;

import com.demo.config.datasource.dynamic.InfoOfDruidDataSourceConfig;
import lombok.Data;

/**
 * @autor 杨瑞
 * @date 2019/6/6 14:41
 */
@Data
public class InfoOfEntity {
    private String entityName;
    private String entityAlias;
    //判断是否是视图，如果是视图，则为true，默认为false;
    private boolean view;

    private InfoOfDruidDataSourceConfig config;

    @Override
    public String toString() {
        return "InfoOfEntity{" +
                "entityName='" + entityName + '\'' +
                ", entityAlias='" + entityAlias + '\'' +
                ", view=" + view +
                ", config=" + config +
                '}';
    }
}
