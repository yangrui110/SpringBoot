package com.yangframe.config.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * @autor 杨瑞
 * @date 2019/6/13 21:22
 */
public class Util {

    public static String getRandUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static String getTimeId(){
        SimpleDateFormat s = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        return s.format(new Date());
    }

    public static String getTimeRandId(){
        SimpleDateFormat s = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        return s.format(new Date())+getRand();
    }

    private static String getRand(){
        StringBuilder builder = new StringBuilder();
        for(int i=0;i<5;i++){
            builder.append(Math.floor(Math.random())*10);
        }

        return builder.toString();
    }
}
