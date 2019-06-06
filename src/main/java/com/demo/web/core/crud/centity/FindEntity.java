package com.demo.web.core.crud.centity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @autor 杨瑞
 * @date 2019/6/6 13:53
 * @describetion 查找的实体条件
 */
@Data
public class FindEntity {

    //实体的名字
    private String entityName;
    //查询条件
    private List<CEntity> condition = new ArrayList<>();
    private Integer start;
    private Integer end;
    private List<COrderBy> orderBy;
    //传入的数据
    private Map data;
}
