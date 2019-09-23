package com.yangframe.web.core.crud.centity;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @autor 杨瑞
 * @date 2019/9/23 19:25
 */
@Data
public class OneToManyEntity {

    private String entityName;
    private List<Map<String,Object>> data;
    private List<String> mainColumns;
}
