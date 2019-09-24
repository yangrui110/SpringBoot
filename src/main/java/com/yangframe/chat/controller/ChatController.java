package com.yangframe.chat.controller;

import com.alibaba.fastjson.JSONObject;
import com.yangframe.chat.service.ChatService;
import com.yangframe.config.advice.ResultEntity;
import com.yangframe.config.advice.ResultEnum;
import com.yangframe.config.util.MapUtil;
import com.yangframe.config.websocket.WebSocketChat;
import com.yangframe.web.core.crud.service.BaseServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

/**
 * @autor 杨瑞
 * @date 2019/7/14 12:54
 */
@Api("聊天服务")
@Controller
@RequestMapping("chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private BaseServiceImpl baseService;

    @Autowired
    private WebSocketChat webSocketChat;

    @Autowired
    HttpServletRequest request;

    @ApiOperation("发送消息接口")
    @ResponseBody
    @PostMapping("send")
    public ResultEntity chat(@RequestBody Map<String,Object> data) throws IOException {
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("userId", "2");
        jsonObject.put("content", "服务器的内容");
        webSocketChat.sendMsg("10001", "我给客户发消息啦！");
        //chatService.sendMsg(data);
        //ChatSocketHandler.sendMsg(jsonObject.toJSONString());
        return new ResultEntity(ResultEnum.OK, MapUtil.toMap("result", true));
    }

}
