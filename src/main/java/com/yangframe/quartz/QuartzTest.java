package com.yangframe.quartz;

import com.yangframe.config.quartz.QuartzCommonDatabase;
import org.quartz.JobExecutionContext;

import java.util.stream.Stream;

/**
 * @autor 杨瑞
 * @date 2019/9/19 9:44
 */
public class QuartzTest extends QuartzCommonDatabase {
    @Override
    public void jobImp(JobExecutionContext jobExecutionContext) {
        System.out.println("我是定时器");
    }

    public static void main(String[] args) {
        Stream<String> f = Stream.of("test", "t1", "t2", "teeeee", "aaaa", "taaa");
        //以下结果将打印： "test", "t1", "t2", "teeeee"，最后的那个taaa不会进行打印
    }
}
