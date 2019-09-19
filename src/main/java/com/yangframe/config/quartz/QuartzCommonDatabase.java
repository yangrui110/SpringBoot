package com.yangframe.config.quartz;

import com.yangframe.web.core.crud.centity.FindEntity;
import com.yangframe.web.core.crud.service.BaseServiceImpl;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @autor 杨瑞
 * @date 2019/9/18 22:18
 */
public abstract class QuartzCommonDatabase implements Job, ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        BaseServiceImpl baseService = applicationContext.getBean(BaseServiceImpl.class);
        Trigger trigger = jobExecutionContext.getTrigger();
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Map<String,Object> triggerMap=new HashMap<>();
        jobImp(jobExecutionContext);
        triggerMap.put("schedulerTriggerId", trigger.getKey().getName()+"_"+trigger.getKey().getGroup());
        triggerMap.put("schedulerEndTime", dateFormat.format(trigger.getEndTime()));
        triggerMap.put("schedulerPriority", ""+trigger.getPriority());
        Date next = jobExecutionContext.getNextFireTime()==null?null:jobExecutionContext.getNextFireTime();
        if(next!=null) {
            triggerMap.put("schedulerNextFireTime", dateFormat.format(next));
        }else {
            triggerMap.put("schedulerNextFireTime", null);
            //执行完毕；更新job为已完成状态
            JobDetail jobDetail = jobExecutionContext.getJobDetail();
            Map<String,Object> jobMap=new HashMap<>();
            jobMap.put("schedulerJobId", jobDetail.getKey().getName()+"_"+jobDetail.getKey().getGroup());
            jobMap.put("schedulerJobStatus", QuartzJobStatus.FINISHED);
            baseService.update(FindEntity.newInstance().makeData(jobMap).makeEntityName("yangJob"));
        }
        Date previous=jobExecutionContext.getPreviousFireTime()==null?null:jobExecutionContext.getPreviousFireTime();
        if(previous!=null)
        triggerMap.put("schedulerPreviousFireTime", dateFormat.format(previous));
        baseService.update(FindEntity.newInstance().makeEntityName("yangTrigger").makeData(triggerMap));
    }
    public abstract void jobImp(JobExecutionContext jobExecutionContext);

    @Override
    public void setApplicationContext(ApplicationContext applicationContext1) throws BeansException {
        applicationContext= applicationContext1;
    }
}
