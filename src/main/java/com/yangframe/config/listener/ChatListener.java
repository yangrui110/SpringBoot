package com.yangframe.config.listener;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * @autor 杨瑞
 * @date 2019/7/14 12:04
 */
@Component
public class ChatListener {

    Logger logger= LoggerFactory.getLogger(ChatListener.class);

    @EventListener(condition = "#event.JNDI=='chat'")
    public void dealChat(ChatEvent event){
        logger.info("接收到新消息:"+event.getContent());
    }
}
