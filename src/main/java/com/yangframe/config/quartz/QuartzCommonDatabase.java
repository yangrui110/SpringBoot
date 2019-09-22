package com.yangframe.config.quartz;

import com.alibaba.fastjson.JSONObject;
import com.yangframe.config.util.ApplicationContextUtil;
import com.yangframe.config.util.MapUtil;
import com.yangframe.config.util.Util;
import com.yangframe.web.core.crud.centity.ConditionEntity;
import com.yangframe.web.core.crud.centity.FindEntity;
import com.yangframe.web.core.crud.service.BaseServiceImpl;
import com.yangframe.web.core.util.MakeConditionUtil;
import org.quartz.*;
import org.springframework.beans.BeansException;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @autor 杨瑞
 * @date 2019/9/18 22:18
 */
public abstract class QuartzCommonDatabase implements Job  {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        jobImp(jobExecutionContext);
        JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        System.out.println(JSONObject.toJSONString(jobDataMap));
        BaseServiceImpl baseService = ApplicationContextUtil.applicationContext.getBean(BaseServiceImpl.class);
        JobKey jobKey = jobExecutionContext.getJobDetail().getKey();
        Trigger trigger = jobExecutionContext.getTrigger();
        Map toMap = MapUtil.toMap("jobName", jobKey.getName());
        toMap.put("jobGroup", jobKey.getGroup());
        try {
            Trigger.TriggerState triggerState = jobExecutionContext.getScheduler().getTriggerState(trigger.getKey());
            System.out.println(triggerState);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        List<Map<String, Object>> list = baseService.findAllNoPage(FindEntity.newInstance().makeEntityName("quartzTaskView").makeData(MakeConditionUtil.makeCondition(toMap)), new ConditionEntity());
        if(trigger.getNextFireTime()==null&&list.size()>0){
            System.out.println("开始插入quarztRecords表");
            Map map=list.get(0);
            map.put("taskId", Util.getTimeId());
            map.put("createTime",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            baseService.insert(FindEntity.newInstance().makeEntityName("quartzRecords").makeData(map));
        }
        System.out.println(list);
    }
    public abstract void jobImp(JobExecutionContext jobExecutionContext);
}
