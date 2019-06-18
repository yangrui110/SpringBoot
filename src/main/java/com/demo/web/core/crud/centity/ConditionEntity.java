package com.demo.web.core.crud.centity;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @autor 杨瑞
 * @date 2019/5/21 13:21
 * @describetion 经过切面拦截后的条件类
 */
@Data
public class ConditionEntity {

    private String columns;
    private String joins;
    private String mainTable;
    private String mainAlias;
    private String condition;
    private String orderBy;

    private Integer start=1;
    private Integer end =10;
}
