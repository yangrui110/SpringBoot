package com.yangframe.config.quartz;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yangframe.web.core.crud.centity.ConditionEntity;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
     * 从数据库读取，然后初始化定时任务
     * */
    public static void initTask() throws ParseException {
        Scheduler scheduler = applicationContext.getBean(Scheduler.class);
        BaseServiceImpl baseService = applicationContext.getBean(BaseServiceImpl.class);
        //1.获取到所有的jobs
        List<Map<String, Object>> jobTriggers = baseService.findAllNoPage(new FindEntityUtil().newInstance().makeEntityName("TriggerJobs").getFindEntity(), new ConditionEntity());
        for (Map<String,Object> job: jobTriggers) {
            if(QuartzJobStatus.FINISHED.equals(job.get("schedulerJobStatus")))
                continue;
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

    public static Trigger makeTrigger(Map<String,Object> data) throws ParseException {
        if(StringUtils.isEmpty(data.get("schedulerTriggerName"))){
            return null;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        TriggerBuilder<Trigger> trigger = TriggerBuilder.newTrigger();
        if(!StringUtils.isEmpty(data.get("schedulerStartTime"))) {
            trigger.startAt(dateFormat.parse((String) data.get("schedulerStartTime")));
        }else {
            trigger.startNow();
        }
        if(!StringUtils.isEmpty(data.get("schedulerTriggerGroup"))){
            trigger.withIdentity((String)data.get("schedulerTriggerName"), (String)data.get("schedulerTriggerGroup"));
        }else{
            trigger.withIdentity((String)data.get("schedulerTriggerName") );
        }
        if(!StringUtils.isEmpty(data.get("schedulerEndTime"))){
            trigger.endAt(dateFormat.parse((String) data.get("schedulerEndTime")));
        }
        if(!StringUtils.isEmpty(data.get("schedulerPriority")))
            trigger.withPriority(Integer.parseInt((String) data.get("schedulerPriority")));
        if(!StringUtils.isEmpty(data.get("schedulerTriggerDesc")))
            trigger.withDescription((String) data.get("schedulerTriggerDesc"));
        if(!StringUtils.isEmpty(data.get("schedulerCronExpression"))){
            //构造cronTrigger
            trigger.withSchedule(CronScheduleBuilder.cronSchedule((String) data.get("schedulerCronExpression")));
        }else{
            //构造simpleTrigger
            SimpleScheduleBuilder builder=SimpleScheduleBuilder.simpleSchedule();
            if(!StringUtils.isEmpty(data.get("schedulerRepeatInterval"))) {
                int time = Integer.parseInt((String) data.get("schedulerRepeatInterval"));
                builder.withIntervalInSeconds(time);
            }
            if(!StringUtils.isEmpty(data.get("schedulerRepeatCount"))){
               builder.withRepeatCount(Integer.parseInt((String) data.get("schedulerRepeatCount")));
            }
            trigger.withSchedule(builder);
        }
        return trigger.build();
    }
    public static JobDetail makeJob(Map<String,Object> data) throws ClassNotFoundException {
        if(StringUtils.isEmpty(data.get("schedulerJobClass"))||StringUtils.isEmpty(data.get("schedulerJobName"))){
            return null;
        }
        JobBuilder builder = JobBuilder.newJob((Class<? extends Job>) Class.forName((String) data.get("schedulerJobClass")));
        if(!StringUtils.isEmpty(data.get("schedulerJobName"))){
            builder.withIdentity((String)data.get("schedulerJobName"), (String)data.get("schedulerJobGroup"));
        }else builder.withIdentity((String)data.get("schedulerJobName"));
        if(!StringUtils.isEmpty(data.get("schedulerDurable"))){
            builder.storeDurably(Boolean.parseBoolean((String) data.get("schedulerDurable")));
        }
        if(!StringUtils.isEmpty(data.get("schedulerDesc"))){
            builder.withDescription((String) data.get("schedulerDesc"));
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
