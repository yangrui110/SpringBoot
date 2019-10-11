package com.yangframe.chat;

import com.alibaba.fastjson.JSONObject;
import com.yangframe.config.util.MapUtil;

import java.util.Map;

/**
 * @autor 杨瑞
 * @date 2019/9/27 9:04
 */
public class SendMsgAdapter {

    public static JSONObject parseFriendNewMsg(Map msg){
        JSONObject os =new JSONObject();
        os.put("key", "newMsg");
        msg.put("type","friend");
        os.put("data", msg);
        return os;
    }
    public static JSONObject parseGroupNewMsg(Map msg){
        JSONObject os =new JSONObject();
        os.put("key", "newMsg");
        msg.put("type","group");
        os.put("data", msg);
        return os;
    }
    public static JSONObject parseNewChat(Map msg){
        JSONObject os =new JSONObject();
        os.put("key", "newChat");
        os.put("data", msg);
        return os;
    }
}
