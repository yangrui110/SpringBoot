package com.yangframe.chat.service;

import com.yangframe.chat.entity.HistoryEntity;
import com.yangframe.config.advice.ResultEntity;
import com.yangframe.config.advice.ResultEnum;
import com.yangframe.config.util.ApplicationContextUtil;
import com.yangframe.config.util.MapUtil;
import com.yangframe.config.util.Util;
import com.yangframe.web.core.crud.centity.ConditionEntity;
import com.yangframe.web.core.crud.centity.FindEntity;
import com.yangframe.web.core.crud.centity.PageMake;
import com.yangframe.web.core.crud.service.BaseServiceImpl;
import com.yangframe.web.core.util.MakeConditionUtil;
import com.yangframe.web.dao.mysql.ChatDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.*;

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
     * @param historyEntity 查找的是和哪个的历史消息
     * */
    public List<Map<String,Object>> getHistoryMsg(HistoryEntity historyEntity){
        //转换分页参数
        PageMake.makeMysqlPage(historyEntity);
        //查询结果
        List<Map<String, Object>> historyMsg = ApplicationContextUtil.applicationContext.getBean(ChatDao.class).getHistoryMsg(historyEntity);
        Collections.reverse(historyMsg);
        return historyMsg;
    }

    /**
     * @param userId 用户的Id
     * */
    public List getChatList(String userId){
        List<Map<String, Object>> unReads = ApplicationContextUtil.applicationContext.getBean(ChatDao.class).getChatList(userId);  //统计未读的消息数量
        Map condition = MapUtil.toMap("imsChatSendUserLoginId", userId);
        List imsChat = baseService.findAll(FindEntity.newInstance().makeEntityName("imsChatMsgView").makeData(condition), new ConditionEntity());
        //转换数据
        for (Object chat: imsChat){
            Map<String,Object> chatOne = (Map<String, Object>) chat;
            Object num=0;
            for(Map<String,Object> unread: unReads){
                if(unread.get("imsReceiveId").equals(chatOne.get("imsChatToReceiveId"))){
                    num= unread.get("notReadNum");
                }
            }
            ((Map<String, Object>) chat).put("notReadNum", num);
        }
        return imsChat;
    }
}
