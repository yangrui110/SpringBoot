package com.demo.config.applicationEvent;

import com.demo.web.core.xmlEntity.EntityMap;
import org.dom4j.DocumentException;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.context.ApplicationListener;

import java.io.IOException;

/**
 * @autor 杨瑞
 * @date 2019/5/18 15:56
 */
public class ApplicationListenerStart implements ApplicationListener<ApplicationPreparedEvent > {

    @Override
    public void onApplicationEvent(ApplicationPreparedEvent applicationStartingEvent) {
        try {
            EntityMap.readXmlIntoMap();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        System.out.println("初始化加载完毕");
    }
}
