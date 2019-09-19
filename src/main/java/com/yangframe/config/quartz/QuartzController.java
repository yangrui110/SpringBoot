package com.yangframe.config.quartz;

import com.yangframe.config.advice.ResultEntity;
import com.yangframe.config.advice.ResultEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.ParseException;
import java.util.Map;

/**
 * @autor 杨瑞
 * @date 2019/9/19 9:46
 */
@Api(description = "定时器控制类")
@Controller
@RequestMapping("quartz")
public class QuartzController {

    @Autowired
    Scheduler scheduler;

    /**
     * @param mapData 传递过来的JobTriggers对象
     * */
    @ApiOperation("开启某个定时器")
    @ResponseBody
    @PostMapping("startQuartz")
    public ResultEntity startQuartz(@RequestBody Map<String,Object> mapData) throws ClassNotFoundException, ParseException, SchedulerException {
        JobDetail jobDetail = SchedulerManager.makeJob(mapData);
        scheduler.pauseJob(jobDetail.getKey());
        scheduler.resumeJob(jobDetail.getKey());
        return new ResultEntity(ResultEnum.OK, new ModelMap("result", true));
    }
}
