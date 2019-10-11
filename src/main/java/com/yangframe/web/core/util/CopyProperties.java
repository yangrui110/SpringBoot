package com.yangframe.web.core.util;

import com.yangframe.web.core.xmlEntity.ColumnProperty;
import com.yangframe.web.core.xmlEntity.EntityMap;

import java.util.HashMap;
import java.util.Map;

public class CopyProperties {

    public static Map copyMapData(String entityName,Map<String,Object> copyData){
        Map result =new HashMap();
        Map<String, ColumnProperty> allColumns = EntityMap.getAllColumns(entityName);
        allColumns.forEach((k,v)->{
            if(copyData.get(k)!=null)
            result.put(k,copyData.get(k));
        });
        return result;
    }
}
