package com.yangframe.config.applicationEvent;

import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.context.ApplicationListener;

/**
 * @autor 杨瑞
 * @date 2019/5/18 15:56
 */
public class ApplicationListenerStart implements ApplicationListener<ApplicationPreparedEvent > {

    @Override
    public void onApplicationEvent(ApplicationPreparedEvent applicationStartingEvent) {
        /*try {
            EntityMap.readXmlIntoMap();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }*/
        System.out.println("初始化加载完毕");
    }
}
