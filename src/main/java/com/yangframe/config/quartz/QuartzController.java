package com.yangframe.config.quartz;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yangframe.config.advice.ResultEntity;
import com.yangframe.config.advice.ResultEnum;
import com.yangframe.web.core.crud.centity.DelEntity;
import com.yangframe.web.core.crud.centity.DelSelectEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
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
    private QuartzService quartzService;

    @ApiOperation("全部中止")
    @ResponseBody
    @PostMapping("pauseAll")
    public ResultEntity pauseAll() throws SchedulerException {
        quartzService.pauseAll();
        return new ResultEntity(ResultEnum.OK,new ModelMap("result", true));
    }
    @ApiOperation("全部恢复")
    @ResponseBody
    @PostMapping("resumeAll")
    public ResultEntity resumeAll() throws SchedulerException {
        quartzService.resumeAll();
        return new ResultEntity(ResultEnum.OK,new ModelMap("result", true));
    }
    @ApiOperation("只触发某一个（不影响数据库）")
    @ResponseBody
    @PostMapping("triggerJob")
    public ResultEntity triggerJob(@RequestBody Map<String,Object> data) throws SchedulerException {
        String jsonString = JSON.toJSONString(data);
        QuartzEntity quartzEntity = JSONObject.parseObject(jsonString).toJavaObject(QuartzEntity.class);
        quartzService.triggerJob(new JobKey(quartzEntity.getJobName(),quartzEntity.getJobGroup()));
        return new ResultEntity(ResultEnum.OK,new ModelMap("result", true));
    }

    @ApiOperation("中止单个任务")
    @ResponseBody
    @PostMapping("pauseTask")
    public ResultEntity pauseTask(@RequestBody Map<String,Object> data) throws SchedulerException {
        String jsonString = JSON.toJSONString(data);
        QuartzEntity quartzEntity = JSONObject.parseObject(jsonString).toJavaObject(QuartzEntity.class);
        quartzService.pauseTask(quartzEntity);
        return new ResultEntity(ResultEnum.OK,new ModelMap("result", true));
    }
    @ApiOperation("恢复单个任务")
    @ResponseBody
    @PostMapping("resumeTask")
    public ResultEntity resumeTask(@RequestBody Map<String,Object> data) throws SchedulerException {
        String jsonString = JSON.toJSONString(data);
        QuartzEntity quartzEntity = JSONObject.parseObject(jsonString).toJavaObject(QuartzEntity.class);
        quartzService.resumeTask(quartzEntity);
        return new ResultEntity(ResultEnum.OK,new ModelMap("result", true));
    }
    /**
     * 添加一个定时任务
     * */
    @ApiOperation(value = "添加一个定时任务")
    @ResponseBody
    @PostMapping("addAndUpdateTask")
    public ResultEntity addAndUpdateTask(@RequestBody Map<String,Object> data) throws ClassNotFoundException, ParseException, SchedulerException {
        String jsonString = JSON.toJSONString(data);
        QuartzEntity quartzEntity = JSONObject.parseObject(jsonString).toJavaObject(QuartzEntity.class);
        quartzService.addAndUpdateTask(quartzEntity);
        return new ResultEntity(ResultEnum.OK, new ModelMap("result", true));
    }
    /**
     * 添加一个定时任务
     * */
    @ApiOperation(value = "删除一个定时任务")
    @ResponseBody
    @PostMapping("deleteOneTask")
    public ResultEntity deleteOneTask(@RequestBody Map<String,Object> data) throws ClassNotFoundException, ParseException, SchedulerException {
        String jsonString = JSON.toJSONString(data);
        QuartzEntity quartzEntity = JSONObject.parseObject(jsonString).toJavaObject(QuartzEntity.class);
        quartzService.deleteOneTask(quartzEntity);
        return new ResultEntity(ResultEnum.OK, new ModelMap("result", true));
    }
    /**
     * 添加一个定时任务
     * */
    @ApiOperation(value = "删除选择的任务")
    @ResponseBody
    @PostMapping("deleteSelectTasks")
    public ResultEntity deleteSelectTasks(@RequestBody List<Map<String,Object>> mapDatas) throws ClassNotFoundException, ParseException, SchedulerException {
        List<QuartzEntity> quartzEntities=new ArrayList<>();
        for(Map<String,Object> mapOne : mapDatas){
            String jsonString = JSON.toJSONString(mapOne);
            quartzEntities.add(JSONObject.parseObject(jsonString).toJavaObject(QuartzEntity.class));
        }
        quartzService.deleteSelectTasks(quartzEntities);
        return new ResultEntity(ResultEnum.OK, new ModelMap("result", true));
    }
}
