package com.yangframe.config.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Set;

/**
 * @autor 杨瑞
 * @date 2019/7/14 13:17
 */
@Configuration
public class JedisUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    private static JedisPool jedisPool;

    private static volatile Jedis jedis;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext1) throws BeansException {
        applicationContext=applicationContext1;
    }

    public static Jedis getJedis(){
        if (jedis ==null){
            synchronized (Jedis.class){
                if (jedis ==null){
                    jedis = getJedisPool().getResource();
                }
            }
        }
        return jedis;
    }

    public static JedisPool getJedisPool(){
        if (jedisPool ==null){
            synchronized (JedisPool.class){
                if (jedisPool==null){
                    jedisPool = applicationContext.getBean(JedisPool.class);
                }
            }
        }
        return jedisPool;
    }

    /**
     * 根据key查看是否存在
     * @param key
     * @return
     */
    public static boolean hasKey(String key){
        return getJedis().exists(key);
    }

    /**
     * 设置key -value 形式数据
     * @param key
     * @param value
     * @return
     */
    public static String set(String key,String value){
        String result =  getJedis().set(key,value);
        return result;
    }

    /**
     * 设置 一个过期时间
     * @param key
     * @param value
     * @param timeOut 单位秒
     * @return
     */
    public static String set(String key,String value,int timeOut){
        return getJedis().setex(key,timeOut,value);
    }

    /**
     * 根据key获取value
     * @param key
     * @return
     */
    public static String getByKey(String key){
        return getJedis().get(key);
    }

    /**
     * 根据通配符获取所有匹配的key
     * @param pattern
     * @return
     */
    public static Set<String> getKesByPattern(String pattern){
        return getJedis().keys(pattern);
    }

    /**
     * 根据key删除
     * @param key
     */
    public static void delByKey(String key){
        getJedis().del(key);
    }

    /**
     * 根据key获取过期时间
     * @param key
     * @return
     */
    public static long getTimeOutByKey(String key){
        return getJedis().ttl(key);
    }

    /**
     * 清空数据 【慎用啊！】
     */
    public static void flushDB(){
        getJedis().flushDB();
    }

    /**
     * 刷新过期时间
     * @param key
     * @param timeOut
     * @return
     */
    public static long refreshLiveTime(String key,int timeOut){
        return getJedis().expire(key,timeOut);
    }
}
