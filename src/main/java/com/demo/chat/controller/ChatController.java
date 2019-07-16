package com.demo.chat.controller;

import com.alibaba.fastjson.JSONObject;
import com.demo.chat.service.ChatService;
import com.demo.config.advice.ResultEntity;
import com.demo.config.advice.ResultEnum;
import com.demo.config.util.MapUtil;
import com.demo.config.websocket.ChatSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * @autor 杨瑞
 * @date 2019/7/14 12:54
 */
@Controller
@RequestMapping("chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

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

}
