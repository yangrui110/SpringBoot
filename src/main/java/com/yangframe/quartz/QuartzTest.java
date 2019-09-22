package com.yangframe.quartz;

import com.yangframe.config.advice.BaseException;
import com.yangframe.config.quartz.QuartzCommonDatabase;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.Date;
import java.util.logging.SimpleFormatter;

/**
 * @autor 杨瑞
 * @date 2019/9/19 9:44
 */
public class QuartzTest extends QuartzCommonDatabase {

    @Override
    public void jobImp(JobExecutionContext jobExecutionContext) {
        System.out.println("我是执行的防范呀");
        System.out.println(jobExecutionContext.getJobDetail().getJobDataMap());
    }
}
