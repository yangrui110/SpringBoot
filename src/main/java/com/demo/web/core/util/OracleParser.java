package com.demo.web.core.util;

import com.demo.web.core.crud.centity.ConditionEntity;
import com.demo.web.core.crud.centity.FindEntity;

import java.util.HashMap;
import java.util.Map;

public class OracleParser {

    public static Map<String,Object> parseMap(Map<String,Object> mapData){
        Map<String,Object> result=new HashMap<>();
        mapData.forEach((k,v)->{
            result.put("\""+k+"\"",v);
        });
        return result;
    }

    public static String parseEntityName(String entityName){
        return "\""+entityName+"\"";
    }

    public static ConditionEntity parseConditionEntity(ConditionEntity entity){
        entity.setMainTable(parseEntityName(entity.getMainTable()));
        return entity;
    }
}
