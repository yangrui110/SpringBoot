package com.yangframe.quartz;

import com.yangframe.config.quartz.QuartzCommonDatabase;
import org.quartz.JobExecutionContext;

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
