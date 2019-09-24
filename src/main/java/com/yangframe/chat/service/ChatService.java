package com.yangframe.chat.service;

import com.yangframe.config.advice.ResultEntity;
import com.yangframe.config.advice.ResultEnum;
import com.yangframe.config.util.Util;
import com.yangframe.web.core.crud.centity.FindEntity;
import com.yangframe.web.core.crud.service.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @autor 杨瑞
 * @date 2019/7/14 12:54
 */
@Service
public class ChatService {

    @Autowired
    private BaseServiceImpl baseService;

    /**
     * 发送消息
     * @param msg 必传的key值：imsMsgSendUserLoginId，imsMsgReceiveId
     * */
    public void sendMsg(Map<String,Object> msg){
        //随机生成一个msgId
        String msgId = Util.getTimeRandId();
        String chatId = (String) msg.get("imsChatId");
        Map<String,Object> msgUp = new HashMap<>();
        msgUp.put("imsMsgId", msgId);
        msgUp.put("imsMsgContent", msg.get("imsMsgContent"));
        msgUp.put("imsMsgType", msg.get("imsMsgType"));
        msgUp.put("imsMsgCreateTime", msg.get("imsMsgCreateTime"));
        msgUp.put("imsMsgSendUserLoginId", msg.get("imsMsgSendUserLoginId"));
        msgUp.put("imsMsgReceiveId", msg.get("imsMsgReceiveId"));
        baseService.insert(FindEntity.newInstance().makeEntityName("imsMsg").makeData(msgUp));
        //修改会话的信息
        Map<String,Object> chat = new HashMap<>();
        chat.put("imsChatCreateTime", msg.get("imsMsgCreateTime"));
        chat.put("imsChatLastReadTime", msg.get("imsMsgCreateTime"));
        chat.put("imsChatLastMsgId", msgId);
        chat.put("imsChatSendUserLoginId", msg.get("imsMsgSendUserLoginId"));
        chat.put("imsChatToReceiveId", msg.get("imsMsgReceiveId"));
        if(StringUtils.isEmpty(chatId)){
            //新建一个会话
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            chatId=Util.getTimeRandId();
            chat.put("imsChatId", chatId);
            chat.put("imsChatCreateTime", dateFormat.format(new Date()));
            chat.put("imsChatLastReadTime", dateFormat.format(new Date()));
            baseService.insert(FindEntity.newInstance().makeEntityName("imsChat").makeData(chat));
        }else {
            chat.put("imsChatId", chatId);
            baseService.update(FindEntity.newInstance().makeEntityName("imsChat").makeData(chat));
        }
    }

    /**
     * @param findEntity 查找的是和哪个的历史消息
     * */
    public ResultEntity getHistoryMsg(FindEntity findEntity){

        return new ResultEntity(ResultEnum.OK, null);
    }
}
