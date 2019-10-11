package com.yangframe.config.websocket;

import com.alibaba.fastjson.JSONObject;
import com.yangframe.chat.SendMsgAdapter;
import com.yangframe.config.util.ApplicationContextUtil;
import com.yangframe.config.util.MapUtil;
import com.yangframe.config.util.Util;
import com.yangframe.web.core.crud.centity.CombineOperator;
import com.yangframe.web.core.crud.centity.ConditionEntity;
import com.yangframe.web.core.crud.centity.FindEntity;
import com.yangframe.web.core.crud.service.BaseServiceImpl;
import com.yangframe.web.core.util.MakeConditionUtil;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class WebSocketMessageHandle extends TextWebSocketHandler {

    private static Map<Object, WebSocketSession> sessionMap = new HashMap<>();

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        System.out.println("收到新消息:" + message.getPayload());
        BaseServiceImpl baseService = ApplicationContextUtil.applicationContext.getBean(BaseServiceImpl.class);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String curDate = dateFormat.format(new Date());
        JSONObject jsonObject = JSONObject.parseObject(message.getPayload());
        String sendId = jsonObject.getString("imsMsgSendUserLoginId");
        String receiveId = jsonObject.getString("imsMsgReceiveId");
        String content = jsonObject.getString("imsMsgContent");
        String createTime = jsonObject.getString("imsMsgCreateTime")==null?curDate:jsonObject.getString("imsMsgCreateTime");
        String msgType = jsonObject.getString("imsMsgType");
        String receivedType = jsonObject.getString("receivedType");
        String sendIcon = jsonObject.getString("sendIcon");
        String msgId = Util.getTimeRandId();
        //保存至数据库中
        Map<String,Object> msg = new HashMap<>();
        msg.put("imsMsgId", jsonObject.getString("imsMsgId")==null?msgId:jsonObject.getString("imsMsgId"));
        msg.put("imsMsgContent", content);
        msg.put("imsMsgType", msgType);
        msg.put("imsMsgCreateTime", createTime);
        msg.put("imsMsgSendUserLoginId", sendId);
        msg.put("imsMsgReceiveId", receiveId);
        baseService.insert(FindEntity.newInstance().makeEntityName("imsMsg").makeData(msg));
        //检测当前接收者和发送者是否已经存在于会话中
        if("group".equals(receivedType)){
            msg.put("sendIcon",sendIcon);  //存放头像信息
            Map toMap = MapUtil.toMap("imsChatToReceiveId", receiveId);
            List<Map<String, Object>> imsChat = baseService.findAllNoPage(FindEntity.newInstance().makeEntityName("imsGroupChat").makeData(MakeConditionUtil.makeCondition(toMap)), new ConditionEntity());
            toMap.put("imsChatLastMsgId", msgId);
            toMap.put("imsChatToReceiveId", receiveId);
            toMap.put("imsChatSendUserLoginId", sendId);
            if (imsChat.size() == 0) {
                toMap.put("imsChatCreateTime", curDate);
                baseService.insert(FindEntity.newInstance().makeEntityName("imsGroupChat").makeData(toMap));
                sendMsg(sendId, SendMsgAdapter.parseNewChat(toMap).toJSONString());
                //获取到需要推送的
            } else {
                baseService.update(FindEntity.newInstance().makeEntityName("imsGroupChat").makeData(toMap));
            }
            //更新用户群组会话表
            Map userGroup = MapUtil.toMap("imsLastReadTime", curDate);
            userGroup.put("imsGroupId",receiveId);
            userGroup.put("imsUserLoginId",sendId);
            baseService.update(FindEntity.newInstance().makeEntityName("imsUserGroup").makeData(userGroup));
            //给群用户推送新消息
            List<Map<String, Object>> allRelations = baseService.findAllNoPage(FindEntity.newInstance().makeEntityName("imsUserGroup").makeData(MakeConditionUtil.makeCondition(MapUtil.toMap("imsGroupId", receiveId))), new ConditionEntity());
            for(Map<String,Object> relation:allRelations){
                if(!relation.get("imsUserLoginId").equals(sendId))
                    sendMsg((String)relation.get("imsUserLoginId"),SendMsgAdapter.parseGroupNewMsg(msg).toJSONString());
            }
        }else {
            msg.put("sendUserLoginInfoIcon",sendIcon);
            Map toMap = MapUtil.toMap("imsChatSendUserLoginId", sendId);
            toMap.put("imsChatToReceiveId", receiveId);
            Map toMap1 = MapUtil.toMap("imsChatSendUserLoginId", receiveId);
            toMap1.put("imsChatToReceiveId", sendId);
            List ls= new ArrayList();
            ls.add(toMap);ls.add(toMap1);
            List<Map<String, Object>> imsChat = baseService.findAllNoPage(FindEntity.newInstance().makeEntityName("imsFriendChat").makeData(MakeConditionUtil.makeCondition(ls, CombineOperator.OR)), new ConditionEntity());

            if (imsChat.size() == 0) {
                Map result = new HashMap();
                result.put("imsChatLastMsgId", msgId);
                result.put("imsChatToReceiveId", receiveId);
                result.put("imsChatSendUserLoginId", sendId);
                result.put("imsChatCreateTime", curDate);
                baseService.insert(FindEntity.newInstance().makeEntityName("imsFriendChat").makeData(result));
                sendMsg(sendId, SendMsgAdapter.parseNewChat(result).toJSONString());
                //获取到需要推送的
            } else {
                Map<String, Object> objectMap = imsChat.get(0);
                objectMap.put("imsChatLastMsgId", msgId);
                baseService.update(FindEntity.newInstance().makeEntityName("imsFriendChat").makeData(objectMap));
            }
            //更新用户群组会话表
            Map userGroup = MapUtil.toMap("imsLastReadTime", curDate);
            userGroup.put("imsFriendId",receiveId);
            userGroup.put("imsUserLoginId",sendId);
            baseService.update(FindEntity.newInstance().makeEntityName("imsUserFriend").makeData(userGroup));
            //给好友推送新消息
            sendMsg(receiveId,SendMsgAdapter.parseFriendNewMsg(msg).toJSONString());
        }
        //返回给前台，数据插入成功
        JSONObject response = new JSONObject();
        response.put("code", 200);
        response.put("msg", msg);
        sendMsg(sendId, response.toJSONString());
        //获取到所有需要推送的用户
        /*sendMsg(receiveId,SendMsgAdapter.parseNewMsg(msg).toJSONString());
        Map<String, Object> imsUserLoginId = MakeConditionUtil.parseOne("imsUserLoginId", sendId, Operator.NOE_EQUAL);
        Map<String, Object> imsGroupId = MakeConditionUtil.parseOne("imsGroupId", receiveId, Operator.EQUAL);
        List ls= new ArrayList();
        ls.add(imsUserLoginId);ls.add(imsGroupId);
        List<Map<String, Object>> groupUsers = baseService.findAllNoPage(FindEntity.newInstance().makeEntityName("imsUserGroup").makeData(MakeConditionUtil.parseLast(ls)), new ConditionEntity());
        for(Map<String,Object> one:groupUsers){
            sendMsg((String) one.get("imsUserLoginId"),SendMsgAdapter.parseNewMsg(msg).toJSONString());
        }*/
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Object userLoginId = session.getAttributes().get("userLoginId");
        WebSocketSession session1 = sessionMap.get(userLoginId);
        if(session1==null)
            sessionMap.put(userLoginId, session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Object userLoginId = session.getAttributes().get("userLoginId");
        if(sessionMap.containsKey(userLoginId))
            sessionMap.remove(userLoginId);
    }
    public static void sendMsg(String key, String msg) throws IOException {
        if(sessionMap.containsKey(key))
            sessionMap.get(key).sendMessage(new TextMessage(msg));
    }
}
