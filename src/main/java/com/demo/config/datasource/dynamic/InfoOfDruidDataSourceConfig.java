package com.demo.config.datasource.dynamic;

import com.alibaba.druid.pool.DruidDataSource;

/**
 * @autor 杨瑞
 * @date 2019/6/13 21:56
 */
public class InfoOfDruidDataSourceConfig {
    private DruidDataSource druidDataSource;
    private String sourceDaoPath;
    private String sourceDaoXmlPath;
    private String sourceType;
    private String sourceBeanName;
    private String sourceBeanXmlPath;
    private Class daoBaseClassName;

    public Class getDaoBaseClassName() {
        return daoBaseClassName;
    }

    public void setDaoBaseClassName(Class daoBaseClassName) {
        this.daoBaseClassName = daoBaseClassName;
    }

    public DruidDataSource getDruidDataSource() {
        return druidDataSource;
    }

    public void setDruidDataSource(DruidDataSource druidDataSource) {
        this.druidDataSource = druidDataSource;
    }

    public String getSourceDaoPath() {
        return sourceDaoPath;
    }

    public void setSourceDaoPath(String sourceDaoPath) {
        this.sourceDaoPath = sourceDaoPath;
    }

    public String getSourceDaoXmlPath() {
        return sourceDaoXmlPath;
    }

    public void setSourceDaoXmlPath(String sourceDaoXmlPath) {
        this.sourceDaoXmlPath = sourceDaoXmlPath;
    }

    public String getSourceBeanXmlPath() {
        return sourceBeanXmlPath;
    }

    public void setSourceBeanXmlPath(String sourceBeanXmlPath) {
        this.sourceBeanXmlPath = sourceBeanXmlPath;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getSourceBeanName() {
        return sourceBeanName;
    }

    public void setSourceBeanName(String sourceBeanName) {
        this.sourceBeanName = sourceBeanName;
    }
}
