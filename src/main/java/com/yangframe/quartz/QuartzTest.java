package com.yangframe.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @autor 杨瑞
 * @date 2019/9/19 9:44
 */
public class QuartzTest implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        System.out.println("定时哈哈哈哈");
    }
}
