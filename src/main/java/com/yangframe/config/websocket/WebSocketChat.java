package com.yangframe.config.websocket;

import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @autor 杨瑞
 * @date 2019/9/24 17:31
 */
@ServerEndpoint("/webSocketChat")
@Component
public class WebSocketChat {

    private static Map<String, Session> sessionMap = new HashMap<>();

    @OnMessage
    public void onMessage(String text) {
        System.out.println("收到新消息:" + text);
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
        sessionMap.get(key).getBasicRemote().sendText(msg);
    }
}
