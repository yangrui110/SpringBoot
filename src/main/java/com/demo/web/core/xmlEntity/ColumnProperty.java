package com.demo.web.core.xmlEntity;

import lombok.Data;

/**
 * @autor 杨瑞
 * @date 2019/5/27 20:33
 */
@Data
public class ColumnProperty {

    private String column;
    private String alias;
    private String tableName;
    private String tableAlias;
    private String tableMemberAlias;

    //列描述信息
    private String describetion;
}
