package com.yangframe.config.quartz;

import com.alibaba.fastjson.JSONObject;
import com.yangframe.config.advice.BaseException;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @autor 杨瑞
 * @date 2019/9/20 19:29
 */
@Service
public class QuartzService {

    @Autowired
    Scheduler scheduler;

    public void addAndUpdateTask(QuartzEntity quartzEntity) throws ClassNotFoundException, ParseException, SchedulerException {
        //构造trigger
        Trigger trigger = null;
        TriggerKey triggerKey= null;
        if(!StringUtils.isEmpty(quartzEntity.getTriggerName())&&!StringUtils.isEmpty(quartzEntity.getTriggerGroup()))
            triggerKey=new TriggerKey(quartzEntity.getTriggerName(), quartzEntity.getTriggerGroup());
        else if(!StringUtils.isEmpty(quartzEntity.getTriggerName()))
            triggerKey = new TriggerKey(quartzEntity.getTriggerName());
        else triggerKey=null;
        if(scheduler.checkExists(triggerKey)){
            System.out.println("我正在更新");
            //就执行更新的方法
            TriggerBuilder<? extends Trigger> triggerBuilder = scheduler.getTrigger(triggerKey).getTriggerBuilder();
            trigger = makeTriggerBuilder(triggerBuilder, quartzEntity).build();
            scheduler.rescheduleJob(triggerKey,trigger);
        }else {
            System.out.println("我正在新增");
            //判断属性不可空
            if (StringUtils.isEmpty(quartzEntity.getJobClassName()))
                throw new BaseException(506, "job的实现类名不能为空！");
            JobKey jobKey = null;
            if (!StringUtils.isEmpty(quartzEntity.getJobName()) && !StringUtils.isEmpty(quartzEntity.getJobGroup()))
                jobKey = new JobKey(quartzEntity.getJobName(), quartzEntity.getJobGroup());
            else if (!StringUtils.isEmpty(quartzEntity.getJobName()))
                jobKey = new JobKey(quartzEntity.getJobName());
            else jobKey = null;
            //1.制作jobDetail
            JobBuilder jobBuilder = JobBuilder.newJob((Class<? extends Job>) Class.forName(quartzEntity.getJobClassName()));
            if (jobKey != null)
                jobBuilder.withIdentity(jobKey);
            if (!StringUtils.isEmpty(quartzEntity.getJobDesc()))
                jobBuilder.withDescription(quartzEntity.getJobDesc());
            if (!StringUtils.isEmpty(quartzEntity.getIsDurable()))
                jobBuilder.storeDurably(Boolean.parseBoolean(quartzEntity.getIsDurable()));
            if (!StringUtils.isEmpty(quartzEntity.getIsUpdateData()))
                jobBuilder.requestRecovery(quartzEntity.getRequestRecovery());
            if (!StringUtils.isEmpty(quartzEntity.getJobData()))
                jobBuilder.setJobData(new JobDataMap((Map<?, ?>) JSONObject.parse(quartzEntity.getJobData())));
            JobDetail jobDetail = jobBuilder.build();

            TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();
            if (triggerKey != null)
                triggerBuilder.withIdentity(triggerKey);
            trigger = makeTriggerBuilder(triggerBuilder, quartzEntity).build();
            scheduler.scheduleJob(jobDetail, trigger);
        }
    }

    private TriggerBuilder makeTriggerBuilder(TriggerBuilder triggerBuilder,QuartzEntity quartzEntity) throws ParseException {

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
            triggerBuilder.withSchedule(CronScheduleBuilder
                    .cronSchedule(quartzEntity.getCronTriggerExpression())
                    .withMisfireHandlingInstructionDoNothing()
            );
        }else {
            triggerBuilder.withSchedule(SimpleScheduleBuilder
                    .simpleSchedule()
                    .withRepeatCount(quartzEntity.getSimpleRepeatCount())
                    .withIntervalInSeconds(quartzEntity.getSimpleRepeatInterval())
                    .withMisfireHandlingInstructionIgnoreMisfires()
            );
        }
        return triggerBuilder;
    }

    public void pauseTask(QuartzEntity quartzEntity) throws SchedulerException {
        TriggerKey triggerKey=new TriggerKey(quartzEntity.getTriggerName(),quartzEntity.getTriggerGroup());
        if(scheduler.checkExists(triggerKey))
            scheduler.pauseTrigger(triggerKey);
    }
    public void resumeTask(QuartzEntity quartzEntity) throws SchedulerException {
        TriggerKey triggerKey=new TriggerKey(quartzEntity.getTriggerName(),quartzEntity.getTriggerGroup());
        if(scheduler.checkExists(triggerKey))
            scheduler.resumeTrigger(triggerKey);
    }
    /**
     * <p>触发一次</p>
     * */
    public void triggerJob(JobKey jobKey) throws SchedulerException {
        scheduler.triggerJob(jobKey);
    }

    /**
     * 停止调度Job任务
     * @param triggerkey
     * @return
     * @throws SchedulerException
     */
    public  boolean unscheduleJob(TriggerKey triggerkey)
            throws SchedulerException{
        return scheduler.unscheduleJob(triggerkey);
    }

    /**
     * 停止调度多个触发器相关的job
     * @param triggerKeylist
     * @return
     * @throws SchedulerException
     */
    public  boolean unscheduleJobs(List<TriggerKey> triggerKeylist) throws SchedulerException {
        return scheduler.unscheduleJobs(triggerKeylist);
    }
    /**
     * Remove (delete) the Trigger with the given key,
     * and store the new given one -which must be associated with the same job
     * (the new trigger must have the job name & group specified) - however,
     * the new trigger need not have the same name as the old trigger.
     * @param triggerkey
     * @param newTrigger
     * @return
     * @throws SchedulerException
     */
    public Date rescheduleJob(TriggerKey triggerkey, Trigger newTrigger)
            throws SchedulerException{
        return scheduler.rescheduleJob(triggerkey, newTrigger);
    }
    /**
     * 停止所有的触发器,及时有新的trigger，也会被暂停
     * */
    public void pauseAll() throws SchedulerException {
        scheduler.pauseAll();
    }
    /**
     * 恢复所有的任务
     * */
    public void resumeAll() throws SchedulerException {
        scheduler.resumeAll();
    }
    /**
     * 批量删除
     * */
    public void deleteSelectTasks(List<QuartzEntity> quartzEntities) throws SchedulerException {
        List<JobKey> jobKeys = new ArrayList<>();
        for(QuartzEntity quartzEntity:quartzEntities){
            jobKeys.add(new JobKey(quartzEntity.getJobName(),quartzEntity.getJobGroup()));
        }
        scheduler.deleteJobs(jobKeys);
    }
    /**
     * 删除一个
     * */
    public void deleteOneTask(QuartzEntity quartzEntity) throws SchedulerException {
        scheduler.deleteJob(new JobKey(quartzEntity.getJobName(),quartzEntity.getJobGroup()));
    }
}
