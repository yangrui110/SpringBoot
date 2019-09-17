package com.yangframe.config.util;

import java.util.UUID;

/**
 * @autor 杨瑞
 * @date 2019/6/13 21:22
 */
public class Util {

    public static String getRandUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}
