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
        return s.format(new Date())+getRand(5);
    }

    public static String getRand(int len){
        StringBuilder builder = new StringBuilder();
        for(int i=0;i<len;i++){
            int floor = (int)(Math.floor(Math.random() * 10));
            builder.append(floor);
        }

        return builder.toString();
    }
}
