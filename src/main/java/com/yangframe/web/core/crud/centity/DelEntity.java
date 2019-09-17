package com.yangframe.web.core.crud.centity;

import lombok.Data;

import java.util.Map;

/**
 * @autor 杨瑞
 * @date 2019/9/14 23:07
 */
@Data
public class DelEntity {
    private String entityName;
    private Map<String,Object> mapDatas;
}
