package com.yangframe.web.core.util;

import com.yangframe.web.core.crud.centity.CombineOperator;
import com.yangframe.web.core.crud.centity.Operator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 制作条件的工具类，返回的Map<String,Object>
 * */
public class MakeConditionUtil {

    public static Map<String,Object> makeCondition(String key,Object object){

        List ls=new ArrayList<>();
        ls.add(parseOne(key,object));
        return parseLast(ls);
    }

    private static Map<String,Object> parseOne(String key,Object value){
        HashMap map=new HashMap();
        map.put("left",key);
        map.put("right",value);
        return map;
    }

    public static Map<String,Object> parseOne(String key,Object value,String operator){
        Map<String,Object> map=parseOne(key,value);
        map.put("operator",operator==null? Operator.EQUAL:operator);
        return map;
    }
    public static Map<String,Object> parseLast(List ls){
        HashMap hashMap=new HashMap();
        hashMap.put("conditionList",ls);
        return hashMap;
    }
    public static Map<String,Object> parseLast(List ls,String combine){
        Map<String, Object> parseLast = parseLast(ls);
        parseLast.put("combine",combine==null? CombineOperator.AND:combine);
        return parseLast;
    }
    public static Map<String,Object> makeCondition(String key1,String value1,String key2,String value2){
        List ls=new ArrayList();
        ls.add(parseOne(key1,value1));
        ls.add(parseOne(key2,value2));
        return parseLast(ls);
    }

    public static Map<String,Object> makeCondition(Object... values){
        int i=0;
        List ls=new ArrayList();
        while(i+2<=values.length){
            ls.add(parseOne((String) values[i],values[i+1]));
            i+=2;
        }
        return parseLast(ls);
    }

    public static Map<String,Object> makeCondition(Map<String,Object> map1){
        List<Map<String,Object>> ls=new ArrayList<>();
        map1.forEach((key,value)->{
            ls.add(parseOne(key,value));
        });
        return parseLast(ls);
    }

    public static Map<String,Object> makeCondition(Map<String,Object> mapLeft,Map<String,Object> mapRight,String combine){
        Map<String, Object> objectMap1 = makeCondition(mapLeft);
        Map<String, Object> objectMap2 = makeCondition(mapRight);
        List<Map<String,Object>> ls=new ArrayList<>();
        ls.add(objectMap1);
        ls.add(objectMap2);
        return parseLast(ls,combine);
    }
    /**
     * @param lists 默认存储的Map格式是：{key:value}
     * */
    public static Map<String,Object> makeCondition(List<Map<String,Object>> lists,String combine){
        List ls=new ArrayList();
        for(Map<String,Object> map: lists){
            ls.add(makeCondition(map));
        }
        return parseLast(ls,combine);
    }
    /**
     * @param lists 默认存储的Map格式是：{key:value}
     * */
    public static Map<String,Object> makeCondition(List<Map<String,Object>> lists){
        List ls=new ArrayList();
        for(Map<String,Object> map: lists){
            ls.add(makeCondition(map));
        }
        return parseLast(ls);
    }
    /**
     * @param lists1 Map集合
     * @param lists2 Map集合
     * @param combine 结合关系
     * @see CombineOperator 获取详细的combine信息
     * */
    public static Map<String,Object> makeCondition(List<Map<String,Object>> lists1,List<Map<String,Object>> lists2,String combine){
        Map<String, Object> left = makeCondition(lists1);
        Map<String, Object> right = makeCondition(lists2);
        List ls=new ArrayList();
        ls.add(left);
        ls.add(right);
        return parseLast(ls,combine);
    }


}
