package com.yangframe.config.quartz;

import lombok.Data;
import org.quartz.Trigger;

/**
 * @autor 杨瑞
 * @date 2019/9/20 19:12
 * 定义quartz从前台传递过来的参数
 */
@Data
public class QuartzEntity {
    //包含的job属性
    private String jobName;
    private String jobGroup;
    private String jobDesc;
    private String jobClassName;
    private String isDurable;
    private String isUpdateData;
    private Boolean requestRecovery;
    private String jobData;
    //包含的触发器属性
    private String triggerName;
    private String triggerGroup;
    private String triggerDesc;
    private Integer triggerPriority;
    private String triggerStartTime;
    private String triggerEndTime;
    private Trigger.TriggerState triggerState;
    //cron触发器的具体属性
    private String cronTriggerExpression;
    //simple触发器的具体属性
    private Integer simpleRepeatCount;
    private Integer simpleRepeatInterval;
}
