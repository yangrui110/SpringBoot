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

import javax.net.ssl.SSLSocketFactory;
import javax.websocket.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @autor 杨瑞
 * @date 2019/9/24 17:31
 */
//@ServerEndpoint(value="/webSocketChat")
//@Component
public class WebSocketChat {


    private static Map<String, Session> sessionMap = new HashMap<>();
    /**
     * 传入的是JSONObject
     * */
    @OnMessage
    public void onMessage(String text) throws IOException {
        System.out.println("收到新消息:" + text);
        BaseServiceImpl baseService = ApplicationContextUtil.applicationContext.getBean(BaseServiceImpl.class);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String curDate = dateFormat.format(new Date());
        JSONObject jsonObject = JSONObject.parseObject(text);
        String sendId = jsonObject.getString("imsMsgSendUserLoginId");
        String receiveId = jsonObject.getString("imsMsgReceiveId");
        String content = jsonObject.getString("imsMsgContent");
        String createTime = jsonObject.getString("imsMsgCreateTime")==null?curDate:jsonObject.getString("imsMsgCreateTime");
        String msgType = jsonObject.getString("imsMsgType");
        String receivedType = jsonObject.getString("receivedType");
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
        }else {
            Map toMap = MapUtil.toMap("imsChatSendUserLoginId", sendId);
            toMap.put("imsChatToReceiveId", receiveId);
            Map toMap1 = MapUtil.toMap("imsChatSendUserLoginId", receiveId);
            toMap.put("imsChatToReceiveId", sendId);
            List ls= new ArrayList();
            ls.add(toMap);ls.add(toMap1);
            List<Map<String, Object>> imsChat = baseService.findAllNoPage(FindEntity.newInstance().makeEntityName("imsFriendChat").makeData(MakeConditionUtil.makeCondition(ls, CombineOperator.OR)), new ConditionEntity());

            if (imsChat.size() == 0) {
                Map result = new HashMap();
                result.put("imsChatLastMsgId", msgId);
                result.put("imsChatToReceiveId", receiveId);
                result.put("imsChatSendUserLoginId", sendId);
                result.put("imsChatCreateTime", curDate);
                baseService.insert(FindEntity.newInstance().makeEntityName("imsFriendChat").makeData(toMap));
                sendMsg(sendId, SendMsgAdapter.parseNewChat(toMap).toJSONString());
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

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("连接成功");
        Map<String, List<String>> requestParameterMap = session.getRequestParameterMap();
        List<String> list = requestParameterMap.get("userLoginId");
        if (list.size() > 0)
            sessionMap.put(list.get(0), session);
        System.out.println(session.getRequestURI());
    }

    @OnError
    public void onError(Session session, Throwable error) {
        System.out.println("出错啦");
        error.printStackTrace();
    }

    @OnClose
    public void onClose(Session session) {
        String userLoginId = session.getPathParameters().get("userLoginId");
        sessionMap.remove(userLoginId);
    }

    public void sendMsg(String key, String msg) throws IOException {
        if(sessionMap.containsKey(key))
            sessionMap.get(key).getBasicRemote().sendText(msg);
    }
}
