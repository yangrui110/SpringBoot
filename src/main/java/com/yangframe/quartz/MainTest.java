package com.yangframe.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

public class MainTest {
    public static void main(String[] args) throws SchedulerException {
        JobDetail jobDetail = JobBuilder.newJob(JobImpl.class).withIdentity("name1", "group1").build();
        SimpleTrigger trigger1 = TriggerBuilder.newTrigger().withIdentity("trigger1").startNow().withSchedule(SimpleScheduleBuilder.repeatSecondlyForever(2).repeatForever()).build();
        StdSchedulerFactory factory = new StdSchedulerFactory();
        Scheduler scheduler = factory.getScheduler();
        scheduler.scheduleJob(jobDetail,trigger1);
        scheduler.start();
    }
}
