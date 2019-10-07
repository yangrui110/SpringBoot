package com.yangframe.chat.controller;

import com.alibaba.fastjson.JSONObject;
import com.yangframe.chat.SendMsgAdapter;
import com.yangframe.chat.entity.HistoryEntity;
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
import java.util.HashMap;
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
        Map<String,Object> msgUp = new HashMap<>();
        msgUp.put("imsMsgId", "99990");
        msgUp.put("imsMsgContent","我是啊啊哈华盛顿巴适得很把进度表");
        msgUp.put("imsMsgType", "text");
        msgUp.put("imsMsgCreateTime", "2019-01-08 00:00:00");
        msgUp.put("imsMsgSendUserLoginId", "10001");
        msgUp.put("imsMsgReceiveId", "10002");
        webSocketChat.sendMsg("10001", SendMsgAdapter.parseNewMsg(msgUp).toJSONString());
        //chatService.sendMsg(data);
        //ChatSocketHandler.sendMsg(jsonObject.toJSONString());
        return new ResultEntity(ResultEnum.OK, MapUtil.toMap("result", true));
    }

    @ApiOperation("获取会话列表的接口")
    @GetMapping("getChatLists")
    @ResponseBody
    public ResultEntity getChatLists(@RequestParam String userLoginId){

        return new ResultEntity(ResultEnum.OK, chatService.getChatList(userLoginId));
    }

    @ApiOperation("获取历史消息")
    @PostMapping("getHistoryMsg")
    @ResponseBody
    public ResultEntity getHistoryMsg(@RequestBody HistoryEntity historyEntity){

        return new ResultEntity(ResultEnum.OK, chatService.getHistoryMsg(historyEntity));
    }

    @ApiOperation("获取群组详情")
    @GetMapping("getDetailGroup")
    @ResponseBody
    public ResultEntity getDetailGroup(@RequestParam  String groupId){
        return new ResultEntity(ResultEnum.OK,chatService.getDetailGroup(groupId));
    }

    @ApiOperation("处理入群申请")
    @PostMapping("judeGroup")
    @ResponseBody
    public ResultEntity judeGroup(@RequestBody Map<String,Object> mapData){
        chatService.judeGroup(mapData);
        return new ResultEntity(ResultEnum.OK,MapUtil.toMap("result",true));
    }
    @ApiOperation("处理好友申请")
    @PostMapping("judeFriend")
    @ResponseBody
    public ResultEntity judeFriend(@RequestBody Map<String,Object> mapData){
        chatService.judeFriend(mapData);
        return new ResultEntity(ResultEnum.OK,MapUtil.toMap("result",true));
    }
    @ApiOperation("删除好友以及会话")
    @PostMapping("delFriend")
    @ResponseBody
    public ResultEntity delFriend(@RequestBody Map<String,Object> mapData){
        chatService.delFriend(mapData);
        return new ResultEntity(ResultEnum.OK,MapUtil.toMap("result",true));
    }
    @ApiOperation("退群以及删除会话")
    @PostMapping("delUserGroup")
    @ResponseBody
    public ResultEntity delUserGroup(@RequestBody Map<String,Object> mapData){
        chatService.delUserGroup(mapData);
        return new ResultEntity(ResultEnum.OK,MapUtil.toMap("result",true));
    }
}
