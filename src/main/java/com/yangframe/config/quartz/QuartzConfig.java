package com.yangframe.config.quartz;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.Properties;

/**
 * @autor 杨瑞
 * @date 2019/9/20 15:31
 */
@Configuration
public class QuartzConfig {


    @Bean
    public Properties quartzProperties() throws IOException {
        PropertiesFactoryBean factoryBean=  new PropertiesFactoryBean();
        factoryBean.setLocation(new ClassPathResource("quartz.properties"));
        factoryBean.afterPropertiesSet();
        return factoryBean.getObject();
    }
    @Bean
    public Scheduler scheduler() throws IOException, SchedulerException {
        SchedulerFactory schedulerFactory = new StdSchedulerFactory(quartzProperties());
        Scheduler scheduler = schedulerFactory.getScheduler();
        return scheduler;
    }

}
