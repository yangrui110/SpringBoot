package com.yangframe.config.applicationEvent;

import com.yangframe.config.util.ApplicationContextUtil;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class ApplicationStop implements ServletContextListener {
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        /*Scheduler scheduler = ApplicationContextUtil.applicationContext.getBean(Scheduler.class);
        System.out.println("执行关闭实践1");
        if(scheduler!=null){
            //执行关闭
            System.out.println("关闭2");
            try {
                scheduler.shutdown();
                Thread.sleep(1000);
            } catch (SchedulerException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }*/
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("我正在初始化新消息");
    }
    /*@Override
    public void onApplicationEvent(ContextClosedEvent contextClosedEvent) {
        Scheduler scheduler = contextClosedEvent.getApplicationContext().getBean(Scheduler.class);
        System.out.println("执行关闭实践1");
        if(scheduler!=null){
            //执行关闭
            System.out.println("关闭2");
            try {
                scheduler.shutdown();
                Thread.sleep(1000);
            } catch (SchedulerException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }*/
}
