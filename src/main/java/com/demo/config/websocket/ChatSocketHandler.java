package com.demo.config.websocket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.demo.config.advice.BaseException;
import com.demo.web.core.crud.service.BaseServiceImpl;
import org.springframework.util.StringUtils;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @autor 杨瑞
 * @date 2019/7/14 20:20
 */
public class ChatSocketHandler extends TextWebSocketHandler {

    public static Map<String, WebSocketSession> sessions=new HashMap<>();  //存储界面对应的会话，用Id作为type_Id作为key

    /**
     * 向前端发送消息
     * @param msg 对应的消息JSON字符串
     * */
    public static void sendMsg(String msg) throws IOException {
        if(StringUtils.isEmpty(msg))
            throw new BaseException(306,"发送的消息不能为空");
        JSONObject jsonObject = JSON.parseObject(msg);
        WebSocketSession socketSession = sessions.get(jsonObject.getString("user_id"));
        if(socketSession!=null)
            socketSession.sendMessage(new TextMessage("新消息"));
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String userId = (String) session.getAttributes().get("userId");
        sessions.put(userId, session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String userId = (String) session.getAttributes().get("userId");
        sessions.remove(userId);
    }
}
