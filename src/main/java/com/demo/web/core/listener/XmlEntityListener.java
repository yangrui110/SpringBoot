package com.demo.web.core.listener;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.stereotype.Component;

/**
 * @autor 杨瑞
 * @date 2019/5/11 16:26
 * <p>将定义的实体数据存入static变量中去</p>
 */
@Component
public class XmlEntityListener implements ApplicationListener {
    @Override
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        if (applicationEvent instanceof ContextStartedEvent){
            System.out.println("我已经启动啦");
        }
    }
}
