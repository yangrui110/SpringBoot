package com.yangframe.config.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @autor 杨瑞
 * @date 2019/7/14 13:01
 */
@Data
@Configuration
public class RedisProperties {

    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.port}")
    private Integer port;
    @Value("${spring.redis.jedis.pool.max-active}")
    private Integer maxActive;
    @Value("${spring.redis.jedis.pool.max-wait}")
    private String maxWait;
    @Value("${spring.redis.jedis.pool.min-idle}")
    private Integer minIdle;

    /**
     * redis存储数据模式：
     * 用户信息：user_userId
     * 群组信息：group_groupId
     * 消息记录：chat_userId_groupId
     * */
    @Bean
    public JedisPool redisPool(){
        JedisPoolConfig config=new JedisPoolConfig();
        config.setMinIdle(minIdle==null?1:minIdle);
        config.setMaxIdle(maxActive==null?10:maxActive);
        JedisPool pool=new JedisPool(config,host,port);
        return pool;
    }
}
