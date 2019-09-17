package com.yangframe.web.core.util;

import com.yangframe.web.core.crud.centity.FindEntity;

import java.util.Map;

/**
 * @autor 杨瑞
 * @date 2019/7/14 13:51
 */
public class FindEntityUtil {

    private FindEntity findEntity;

    public FindEntityUtil newInstance(){
        if (this.findEntity==null)
            this.findEntity = new FindEntity();
        return this;
    }

    public FindEntityUtil makeCondition(Map<String,Object> cons) {
        this.findEntity.setCondition(cons);
        return this;
    }
    public FindEntityUtil makeEntityName(String entityName){
        this.findEntity.setEntityName(entityName);
        return this;
    }

    public FindEntityUtil makeData(Map map){
        this.findEntity.setData(map);
        return this;
    }
    public FindEntity getFindEntity() {
        return findEntity;
    }

    public void setFindEntity(FindEntity findEntity) {
        this.findEntity = findEntity;
    }
}
