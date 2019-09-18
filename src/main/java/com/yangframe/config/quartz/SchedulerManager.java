package com.yangframe.config.quartz;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yangframe.web.core.crud.centity.ConditionEntity;
import com.yangframe.web.core.crud.centity.FindEntity;
import com.yangframe.web.core.crud.service.BaseServiceImpl;
import com.yangframe.web.core.util.FindEntityUtil;
import org.quartz.*;
import org.springframework.beans.BeansException;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.thymeleaf.spring5.context.SpringContextUtils;

import java.util.List;
import java.util.Map;

/**
 * @autor 杨瑞
 * @date 2019/9/18 16:51
 */
@Component
public class SchedulerManager implements ApplicationContextAware, ApplicationRunner {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContextParam) throws BeansException {
        applicationContext=applicationContextParam;
    }

    /**
     * 添加一个定时任务
     * */
    public void addOneTask(JobKey jobKey, Class jobClass, Trigger trigger) throws SchedulerException {
        Scheduler scheduler = applicationContext.getBean(Scheduler.class);
        JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(jobKey).build();
        scheduler.scheduleJob(jobDetail, trigger);
    }
    /**
     * 从数据库读取，然后初始化定时任务
     * */
    public static void initTask(){
        Scheduler scheduler = applicationContext.getBean(Scheduler.class);
        BaseServiceImpl baseService = applicationContext.getBean(BaseServiceImpl.class);
        //1.获取到所有的jobs
        List<Map<String, Object>> jobTriggers = baseService.findAllNoPage(new FindEntityUtil().newInstance().makeEntityName("JobTriggers").getFindEntity(), new ConditionEntity());
        for (Map<String,Object> job: jobTriggers) {
            try {
                //构造job
                JobDetail jobDetail = makeJob(job);
            if(jobDetail==null)
                continue;
                //构造trigger
                Trigger trigger = makeTrigger(job);
                if(trigger==null)
                    continue;
                //捕获到异常，防止其影响到其它定时任务的运行
                scheduler.scheduleJob(jobDetail, trigger);
            } catch (SchedulerException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private static Trigger makeTrigger(Map<String,Object> data){
        if(StringUtils.isEmpty(data.get("schedulerTriggerName"))){
            return null;
        }
        if(!StringUtils.isEmpty("schedulerCronExpression")){
            //构造cronTrigger
        }else{
            //构造simpleTrigger
        }
        return null;
    }
    private static JobDetail makeJob(Map<String,Object> data) throws ClassNotFoundException {
        if(StringUtils.isEmpty(data.get("schedulerJobClass"))||StringUtils.isEmpty(data.get("schedulerJobName"))){
            return null;
        }
        JobBuilder builder = JobBuilder.newJob((Class<? extends Job>) Class.forName((String) data.get("schedulerJobClass")));
        if(!StringUtils.isEmpty(data.get("schedulerJobName"))){
            builder.withIdentity((String)data.get("schedulerJobName"), (String)data.get("schedulerJobClass"));
        }else builder.withIdentity((String)data.get("schedulerJobName"));
        if(!StringUtils.isEmpty(data.get("schedulerDurable"))){
            builder.storeDurably(Boolean.parseBoolean((String) data.get("schedulerDurable")));
        }
        if(!StringUtils.isEmpty(data.get("schedulerDesc"))){
            builder.storeDurably((Boolean) data.get("schedulerDesc"));
        }
        if(!StringUtils.isEmpty(data.get("schedulerRequestRecovery"))){
            builder.requestRecovery(Boolean.parseBoolean((String) data.get("schedulerRequestRecovery")));
        }
        if(!StringUtils.isEmpty(data.get("schedulerJobDataMap"))){
            JSONObject jsonObject = JSON.parseObject((String) data.get("schedulerJobDataMap"));
            JobDataMap dataMap=new JobDataMap(jsonObject);
            builder.setJobData(dataMap);
        }
        return builder.build();
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        initTask();
    }
}
