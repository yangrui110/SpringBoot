package com.demo.config.listener;

import lombok.Data;
import org.springframework.context.ApplicationEvent;

/**
 * @autor 杨瑞
 * @date 2019/7/14 12:05
 */
@Data
public class ChatEvent extends ApplicationEvent {

    private String content;
    private final String JNDI;//过滤指定监听
    public ChatEvent(Object source,String content) {
        super(source);
        this.JNDI = (String) source;
        this.content=content;
    }
}
