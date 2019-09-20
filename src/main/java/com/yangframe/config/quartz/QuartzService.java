package com.yangframe.config.quartz;

import com.alibaba.fastjson.JSONObject;
import com.yangframe.config.advice.BaseException;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;

/**
 * @autor 杨瑞
 * @date 2019/9/20 19:29
 */
@Service
public class QuartzService {

    @Autowired
    Scheduler scheduler;

    public void addTask(QuartzEntity quartzEntity) throws ClassNotFoundException, ParseException, SchedulerException {
        //判断属性不可空
        if(StringUtils.isEmpty(quartzEntity.getJobClassName()))
            throw new BaseException(506,"job的实现类名不能为空！");
        JobKey jobKey=null;
        if(!StringUtils.isEmpty(quartzEntity.getJobName())&&!StringUtils.isEmpty(quartzEntity.getJobGroup()))
            jobKey= new JobKey(quartzEntity.getJobName(), quartzEntity.getJobGroup());
        else if(!StringUtils.isEmpty(quartzEntity.getJobName()))
            jobKey=new JobKey(quartzEntity.getJobName());
        else jobKey= null;
        //1.制作jobDetail
        JobBuilder jobBuilder = JobBuilder.newJob((Class<? extends Job>) Class.forName(quartzEntity.getJobClassName()));
        if(jobKey!=null)
            jobBuilder.withIdentity(jobKey);
        if(!StringUtils.isEmpty(quartzEntity.getJobDesc()))
            jobBuilder.withDescription(quartzEntity.getJobDesc());
        if(!StringUtils.isEmpty(quartzEntity.getIsDurable()))
            jobBuilder.storeDurably(Boolean.parseBoolean(quartzEntity.getIsDurable()));
        if(!StringUtils.isEmpty(quartzEntity.getIsUpdateData()))
            jobBuilder.requestRecovery(quartzEntity.getRequestRecovery());
        if(!StringUtils.isEmpty(quartzEntity.getJobData()))
            jobBuilder.setJobData(new JobDataMap((Map<?, ?>) JSONObject.parse(quartzEntity.getJobData())));
        JobDetail jobDetail = jobBuilder.build();
        //构造trigger
        TriggerKey triggerKey= null;
        if(!StringUtils.isEmpty(quartzEntity.getTriggerName())&&!StringUtils.isEmpty(quartzEntity.getTriggerGroup()))
            triggerKey=new TriggerKey(quartzEntity.getTriggerName(), quartzEntity.getTriggerGroup());
        else if(!StringUtils.isEmpty(quartzEntity.getTriggerName()))
            triggerKey = new TriggerKey(quartzEntity.getTriggerName());
        else triggerKey=null;
        TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();
        if(triggerKey!=null)
            triggerBuilder.withIdentity(triggerKey);
        if(!StringUtils.isEmpty(quartzEntity.getTriggerDesc()))
            triggerBuilder.withDescription(quartzEntity.getTriggerDesc());
        if(!StringUtils.isEmpty(quartzEntity.getTriggerPriority()))
            triggerBuilder.withPriority(quartzEntity.getTriggerPriority());
        if(!StringUtils.isEmpty(quartzEntity.getJobData()))
            triggerBuilder.usingJobData(new JobDataMap((Map<?, ?>) JSONObject.parse(quartzEntity.getJobData())));
        if(!StringUtils.isEmpty(quartzEntity.getTriggerStartTime()))
            triggerBuilder.startAt(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(quartzEntity.getTriggerStartTime()));
        else triggerBuilder.startNow();
        if(!StringUtils.isEmpty(quartzEntity.getTriggerEndTime()))
            triggerBuilder.endAt(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(quartzEntity.getTriggerEndTime()));
        if(!StringUtils.isEmpty(quartzEntity.getCronTriggerExpression())){
            triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(quartzEntity.getCronTriggerExpression()));
        }else {
            triggerBuilder.withSchedule(SimpleScheduleBuilder.simpleSchedule().withRepeatCount(quartzEntity.getSimpleRepeatCount()).withIntervalInSeconds(quartzEntity.getSimpleRepeatInterval()));
        }
        Trigger trigger = triggerBuilder.build();
        scheduler.scheduleJob(jobDetail, trigger);
    }
}
