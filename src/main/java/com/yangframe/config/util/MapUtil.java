package com.yangframe.config.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @autor 杨瑞
 * @date 2019/6/16 0:55
 */
public class MapUtil {
    public static Map newInstance(){
        return new HashMap();
    }
    public static Map toMap(String key,Object value){
        HashMap hashMap = new HashMap();
        hashMap.put(key, value);
        return hashMap;
    }
}
