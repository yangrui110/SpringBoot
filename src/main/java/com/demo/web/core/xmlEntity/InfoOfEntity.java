package com.demo.web.core.xmlEntity;

import lombok.Data;

/**
 * @autor 杨瑞
 * @date 2019/6/6 14:41
 */
@Data
public class InfoOfEntity {
    private String entityName;
    private String entityAlias;
    /**对应数据源的类型*
     * @see com.demo.config.datasource.type.DataSourceType
     */
    private String source;
    //判断是否是视图，如果是视图，则为true，默认为false;
    private boolean view;
}
