package com.yangframe.web.core.crud.centity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @autor 杨瑞
 * @date 2019/6/23 21:09
 */
public class DelSelectEntity {

    private String entityName;
    private List<Map<String,Object>> data =new ArrayList<>();

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public List<Map<String, Object>> getDatas() {
        return data;
    }

    public void setDatas(List<Map<String, Object>> datas) {
        this.data = datas;
    }
}
