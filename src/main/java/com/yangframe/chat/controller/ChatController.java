package com.yangframe.chat.controller;

import com.alibaba.fastjson.JSONObject;
import com.yangframe.chat.service.ChatService;
import com.yangframe.config.advice.ResultEntity;
import com.yangframe.config.advice.ResultEnum;
import com.yangframe.config.util.MapUtil;
import com.yangframe.config.websocket.ChatSocketHandler;
import com.yangframe.web.core.crud.centity.ConditionEntity;
import com.yangframe.web.core.crud.service.BaseServiceImpl;
import com.yangframe.web.core.util.FindEntityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @autor 杨瑞
 * @date 2019/7/14 12:54
 */
@Controller
@RequestMapping("chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private BaseServiceImpl baseService;

    @ResponseBody
    @PostMapping("send")
    public ResultEntity chat() throws IOException {
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("userId", "2");
        jsonObject.put("content", "服务器的内容");
        ChatSocketHandler.sendMsg(jsonObject.toJSONString());
        return new ResultEntity(ResultEnum.OK, MapUtil.toMap("result", true));
    }

    @ResponseBody
    @GetMapping("getchatMsg")
    public ResultEntity getchatMsg(@RequestParam("userId")String userId){
        return new ResultEntity(ResultEnum.OK, chatService.getChatMessage(userId));
    }
    @ResponseBody
    @GetMapping("getHistoryMsg")
    public ResultEntity getHistoryMsg(){

        return new ResultEntity(ResultEnum.OK, null);
    }

    /**
     * 获取通讯录列表
     * @param map 数据格式：{userId:"",type:"yh",start:0,end:10}
     * */
    @ResponseBody
    @PostMapping("getFriendOrGroupList")
    public ResultEntity getFriendOrGroupList(@RequestBody  Map<String,Object> map){
        Object userId = map.get("userId");
        Object start = map.get("start")==null?1:map.get("start");
        Object end = map.get("end")==null?10:map.get("end");
        Map toMap = MapUtil.toMap("left", "user_id");
        toMap.put("right", userId);
        Map toMap1 = MapUtil.toMap("left", "userType");
        toMap1.put("right", map.get("userType"));
        Map toMap2 = MapUtil.toMap("left", "groupType");
        toMap2.put("right", map.get("userType"));
        List ls=new ArrayList();
        ls.add(toMap);
        ls.add(toMap1);
        ls.add(toMap2);
        Map conditionList = MapUtil.toMap("conditionList", ls);
        FindEntityUtil entityUtil = new FindEntityUtil().newInstance().makeEntityName("userFriendView").makeCondition(conditionList);
        entityUtil.getFindEntity().setStart((Integer) start);
        entityUtil.getFindEntity().setEnd((Integer) end);
        List serviceAll = baseService.findAll(entityUtil.getFindEntity(), new ConditionEntity());
        return new ResultEntity(ResultEnum.OK, serviceAll,baseService.totalNum(entityUtil.getFindEntity(), new ConditionEntity()));
    }

    @ResponseBody
    @GetMapping("sendData")
    public ResultEntity sendData() throws IOException {
        JSONObject os=new JSONObject();
        os.put("user_id", "2");
        ChatSocketHandler.sendMsg(os.toString());
        return new ResultEntity(ResultEnum.OK, MapUtil.toMap("result", true));
    }
}
