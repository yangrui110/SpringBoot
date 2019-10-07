package com.yangframe.chat.service;

import com.yangframe.chat.entity.HistoryEntity;
import com.yangframe.config.advice.BaseException;
import com.yangframe.config.advice.ResultEntity;
import com.yangframe.config.advice.ResultEnum;
import com.yangframe.config.util.ApplicationContextUtil;
import com.yangframe.config.util.MapUtil;
import com.yangframe.config.util.Util;
import com.yangframe.web.core.crud.centity.*;
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
    //获取群组的详情
    public Map<String,Object> getDetailGroup(String groupId){
        List<Map<String, Object>> allNoPage = baseService.findAllNoPage(FindEntity.newInstance().makeEntityName("imsGroup").makeData(MakeConditionUtil.makeCondition(MapUtil.toMap("imsGroupId", groupId))), new ConditionEntity());
        if(allNoPage.size()<0)
            throw new BaseException(506,"未找到该群组信息");
        //计算群成员总数
        int totalNum = baseService.totalNum(FindEntity.newInstance().makeEntityName("imsUserGroup").makeData(MakeConditionUtil.makeCondition(MapUtil.toMap("imsGroupId", groupId))), new ConditionEntity());
        //获取admin用户
        List ls =new ArrayList();
        ls.add("admin");ls.add("creator");
        Map<String, Object> map1 = MakeConditionUtil.parseOne("imsJobTitle", ls, Operator.IN);
        Map<String, Object> map2 = MakeConditionUtil.parseOne("imsGroupId", groupId, Operator.EQUAL);
        List result =new ArrayList();
        result.add(map1);result.add(map2);
        List admins = baseService.findAll(FindEntity.newInstance().makeEntityName("imsUserGroup").makeData(MakeConditionUtil.parseLast(result)), new ConditionEntity());
        //返回结果
        Map resp = allNoPage.get(0);
        resp.put("admins",admins);
        resp.put("totalNum",totalNum);
        return resp;
    }

    //审批进群的申请
    public void judeGroup(Map<String,Object> mapData){
        Map group= new HashMap();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        group.put("imsGroupId",mapData.get("imsGroupId"));
        group.put("imsUserLoginId",mapData.get("imsUserLoginId"));
        group.put("imsAskReplyTime",dateFormat.format(new Date()));
        group.put("imsAskStatus",mapData.get("preImsAskStatus"));
        baseService.update(FindEntity.newInstance().makeEntityName("imsAskGroup").makeData(group));
        if("agree".equals(mapData.get("preImsAskStatus"))){
            //插入到群组用户表中
            Map userGroup = new HashMap();
            userGroup.put("imsGroupId",mapData.get("imsGroupId"));
            userGroup.put("imsUserLoginId",mapData.get("imsUserLoginId"));
            userGroup.put("imsJobTitle","normal");
            userGroup.put("imsUserGroupCreateTime",dateFormat.format(new Date()));
            baseService.insert(FindEntity.newInstance().makeEntityName("imsUserGroup").makeData(userGroup));
        }
    }

    //处理好友申请
    public void judeFriend(Map<String,Object> mapData){
        Map friend = new HashMap();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String curDate = dateFormat.format(new Date());
        friend.put("imsSendUserId",mapData.get("imsSendUserId"));
        friend.put("imsReceivedId",mapData.get("imsReceivedId"));
        friend.put("imsAskReplyTime",curDate);
        friend.put("imsAskStatus",mapData.get("preImsAskStatus"));
        baseService.update(FindEntity.newInstance().makeEntityName("imsAskFriend").makeData(friend));
        if("agree".equals(mapData.get("preImsAskStatus"))){
            //把数据插入到好友关系表中
            Map map1 = new HashMap();
            map1.put("imsFriendId",mapData.get("imsSendUserId"));
            map1.put("imsUserLoginId",mapData.get("imsReceivedId"));
            map1.put("imsUserFriendCreateTime",curDate);
            map1.put("imsFriendAlias",mapData.get("sendUserName"));
            Map map2 = new HashMap();
            map2.put("imsUserLoginId",mapData.get("imsSendUserId"));
            map2.put("imsFriendId",mapData.get("imsReceivedId"));
            map2.put("imsUserFriendCreateTime",curDate);
            map2.put("imsFriendAlias",mapData.get("receiveUserName"));
            baseService.store(FindEntity.newInstance().makeEntityName("imsUserFriend").makeData(map1));
            baseService.store(FindEntity.newInstance().makeEntityName("imsUserFriend").makeData(map2));
        }
    }
    //删除好友
    public void delFriend(Map<String,Object> mapData){
        Map map1 = new HashMap();
        map1.put("imsFriendId",mapData.get("imsUserLoginId"));
        map1.put("imsUserLoginId",mapData.get("imsFriendId"));
        Map map2 =new HashMap();
        map2.put("imsFriendId",mapData.get("imsFriendId"));
        map2.put("imsUserLoginId",mapData.get("imsUserLoginId"));
        List ls= new ArrayList();
        ls.add(map1);ls.add(map2);
        baseService.delSelect("imsUserFriend",ls);
        delChat(mapData.get("imsUserLoginId"),mapData.get("imsFriendId"));
    }

    public void delUserGroup(Map<String,Object> mapData){
        Map map1 = new HashMap();
        map1.put("imsGroupId",mapData.get("imsGroupId"));
        map1.put("imsUserLoginId",mapData.get("userLoginInfoId"));
        baseService.delete("imsUserGroup",map1);
        delChat(mapData.get("imsGroupId"),mapData.get("userLoginInfoId"));
    }
    private void delChat(Object userId ,Object receiveId){
        //删除两者的会话
        Map map3 = new HashMap();
        map3.put("imsChatSendUserLoginId",userId);
        map3.put("imsChatToReceiveId",receiveId);
        Map map4 =new HashMap();
        map4.put("imsChatSendUserLoginId",receiveId);
        map4.put("imsChatToReceiveId",userId);
        List ls1= new ArrayList();
        ls1.add(map3);ls1.add(map4);
        baseService.delSelect("imsChat",ls1);
    }
}
