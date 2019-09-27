package com.yangframe.config.applicationEvent;

import com.yangframe.config.datasource.dynamic.DynamicDataSource;
import com.yangframe.config.quartz.SchedulerManager;
import org.quartz.Scheduler;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @autor 杨瑞
 * @date 2019/5/18 15:56
 */
public class ApplicationListenerStart implements ApplicationListener<ApplicationStartedEvent > {

    @Override
    public void onApplicationEvent(ApplicationStartedEvent applicationStartingEvent) {
        //System.out.println("初始化加载完毕");
        if(applicationStartingEvent.getApplicationContext().getParent()==null) {
            ConfigurableApplicationContext applicationContext = applicationStartingEvent.getApplicationContext();
            new DynamicDataSource(applicationContext);
        }
    }
}
