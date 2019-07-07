package com.demo.web.core.crud.centity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @autor 杨瑞
 * @date 2019/6/23 21:09
 */
public class DelSelectEntity {

    private String entityName;
    private List<Map> datas =new ArrayList<>();

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public List<Map> getDatas() {
        return datas;
    }

    public void setDatas(List<Map> datas) {
        this.datas = datas;
    }
}
