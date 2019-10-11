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
import com.yangframe.web.core.util.CopyProperties;
import com.yangframe.web.core.util.MakeConditionUtil;
import com.yangframe.web.dao.mysql.ChatDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.ParseException;
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
     *
     * @param msg 必传的key值：imsMsgSendUserLoginId，imsMsgReceiveId
     */
    public void sendMsg(Map<String, Object> msg) {
        //随机生成一个msgId
        String msgId = Util.getTimeRandId();
        String chatId = (String) msg.get("imsChatId");
        Map<String, Object> msgUp = new HashMap<>();
        msgUp.put("imsMsgId", msgId);
        msgUp.put("imsMsgContent", msg.get("imsMsgContent"));
        msgUp.put("imsMsgType", msg.get("imsMsgType"));
        msgUp.put("imsMsgCreateTime", msg.get("imsMsgCreateTime"));
        msgUp.put("imsMsgSendUserLoginId", msg.get("imsMsgSendUserLoginId"));
        msgUp.put("imsMsgReceiveId", msg.get("imsMsgReceiveId"));
        baseService.insert(FindEntity.newInstance().makeEntityName("imsMsg").makeData(msgUp));
        //修改会话的信息
        Map<String, Object> chat = new HashMap<>();
        chat.put("imsChatCreateTime", msg.get("imsMsgCreateTime"));
        chat.put("imsChatLastMsgId", msgId);
        chat.put("imsChatSendUserLoginId", msg.get("imsMsgSendUserLoginId"));
        chat.put("imsChatToReceiveId", msg.get("imsMsgReceiveId"));
        if (StringUtils.isEmpty(chatId)) {
            //新建一个会话
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            chatId = Util.getTimeRandId();
            chat.put("imsChatId", chatId);
            chat.put("imsChatCreateTime", dateFormat.format(new Date()));
            baseService.insert(FindEntity.newInstance().makeEntityName("imsChat").makeData(chat));
        } else {
            chat.put("imsChatId", chatId);
            baseService.update(FindEntity.newInstance().makeEntityName("imsChat").makeData(chat));
        }
    }

    /**
     * @param historyEntity 查找的是和哪个的历史消息
     */
    public List<Map<String, Object>> getHistoryMsg(HistoryEntity historyEntity) {

        //查询结果
        List<Map<String, Object>> historyMsg = null;
        if ("friend".equals(historyEntity.getReceivedType())) {
            //转换分页参数
            PageMake.makeMysqlPage(historyEntity);
            historyMsg = ApplicationContextUtil.applicationContext.getBean(ChatDao.class).getHistoryMsg(historyEntity);
        } else if ("group".equals(historyEntity.getReceivedType())) {
            Map imsMsgReceiveId = MapUtil.toMap("imsMsgReceiveId", historyEntity.getReceiveId());
            COrderBy orderBy = new COrderBy();
            orderBy.setNames("imsMsgCreateTime");
            orderBy.setDirect("desc");
            List o = new ArrayList();
            o.add(orderBy);
            historyMsg = baseService.findAll(FindEntity.newInstance().makeEntityName("imsMsgGroupView").makeData(MakeConditionUtil.makeCondition(imsMsgReceiveId)).makeStart(historyEntity.getStart()).makeEnd(historyEntity.getEnd()).setOrderBy(o), new ConditionEntity());
        }
        Collections.reverse(historyMsg);
        return historyMsg;
    }

    /**
     * @param userId 用户的Id
     */
    public List getChatList(String userId) {
        ChatDao chatDao = ApplicationContextUtil.applicationContext.getBean(ChatDao.class);
        //1.获取到好友会话表
        Map<String, Object> map1 = MakeConditionUtil.parseOne("imsChatSendUserLoginId", userId, Operator.EQUAL);
        Map<String, Object> map2 = MakeConditionUtil.parseOne("imsChatToReceiveId", userId, Operator.EQUAL);
        List ls = new ArrayList();
        ls.add(map1);
        ls.add(map2);
        Map makeCondition = MakeConditionUtil.parseLast(ls, CombineOperator.OR);
        List<Map<String, Object>> friendChat = baseService.findAllNoPage(FindEntity.newInstance().makeEntityName("imsFriendChatMsgView").makeData(makeCondition), new ConditionEntity());
        if(friendChat.size()>0){
            //查找未读的消息数目
            List<Map<String, Object>> friendNotReadNum = chatDao.getFriendNotReadNum(userId);
            //给friendChat赋未读的消息值
            for(Map<String,Object> mapOne : friendChat){
                for(Map<String,Object> notRead:friendNotReadNum){
                    if(mapOne.get("imsUserLoginId")==null||mapOne.get("imsFriendId")==null)
                        mapOne.put("notReadNum",0);
                    else {
                        boolean b1 = mapOne.get("imsUserLoginId").equals(notRead.get("userLoginId")) && mapOne.get("imsFriendId").equals(notRead.get("friendId"));
                        boolean b2 = mapOne.get("imsUserLoginId").equals(notRead.get("friendId")) && mapOne.get("imsFriendId").equals(notRead.get("userLoginId"));
                        if (b1 || b2) {
                            mapOne.put("notReadNum", notRead.get("notReadNum"));
                        }
                    }
                }
            }
        }
        //2.获取到该用户的所有群组
        List<Map<String, Object>> allGroups = baseService.findAllNoPage(FindEntity.newInstance().makeEntityName("imsUserGroupView").makeData(MakeConditionUtil.makeCondition(MapUtil.toMap("imsUserLoginId", userId))), new ConditionEntity());
        //3.获取到这些群组的会话信息
        List gids = new ArrayList();
        for (Map<String, Object> group : allGroups) {
            gids.add(group.get("imsGroupId"));
        }
        List<Map<String, Object>> groupChat = new ArrayList<>();
        if (gids.size() != 0) {
            Map<String, Object> group1 = MakeConditionUtil.parseOne("imsChatToReceiveId", gids, Operator.IN);
            List ln=new ArrayList();
            ln.add(group1);
            groupChat = baseService.findAllNoPage(FindEntity.newInstance().makeEntityName("imsGroupChatMsgView").makeData(MakeConditionUtil.parseLast(ln)), new ConditionEntity());
            //给群组会话增加未读的消息数目
            if(groupChat.size()>0){
                List<Map<String,Object>> groupNotReadNum = chatDao.getGroupNotReadNum(gids,userId);
                for(Map<String,Object> mapOne : groupChat){
                    for(Map<String,Object> notRead:groupNotReadNum){
                        if(mapOne.get("imsGroupId").equals(notRead.get("groupId"))){
                            mapOne.put("notReadNum",notRead.get("notReadNum"));
                        }
                    }
                }
            }
        }
        //4.按照最后的更新时间，进行排序
        friendChat.addAll(groupChat);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Collections.sort(friendChat, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                Object date1 = o1.get("imsLastReadTime");
                Object date2 = o2.get("imsLastReadTime");
                if (date1 == null)
                    return 1;
                if (date2 == null)
                    return -1;
                try {
                    Date start = dateFormat.parse((String) date1);
                    Date end = dateFormat.parse((String) date2);
                    if (start.getTime() < end.getTime())
                        return 1;
                    else if (start.getTime() > end.getTime()) {
                        return -1;
                    } else return 0;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
        //((Map<String, Object>) chat).put("notReadNum", num);

        return friendChat;
    }

    //获取群组的详情
    public Map<String, Object> getDetailGroup(String groupId) {
        List<Map<String, Object>> allNoPage = baseService.findAllNoPage(FindEntity.newInstance().makeEntityName("imsGroup").makeData(MakeConditionUtil.makeCondition(MapUtil.toMap("imsGroupId", groupId))), new ConditionEntity());
        if (allNoPage.size() < 0)
            throw new BaseException(506, "未找到该群组信息");
        //计算群成员总数
        int totalNum = baseService.totalNum(FindEntity.newInstance().makeEntityName("imsUserGroup").makeData(MakeConditionUtil.makeCondition(MapUtil.toMap("imsGroupId", groupId))), new ConditionEntity());
        //获取admin用户
        List admins = getAdmins(groupId);
        //返回结果
        Map resp = allNoPage.get(0);
        resp.put("admins", admins);
        resp.put("totalNum", totalNum);
        return resp;
    }

    public List getAdmins(String groupId) {
        List ls = new ArrayList();
        ls.add("admin");
        ls.add("creator");
        Map<String, Object> map1 = MakeConditionUtil.parseOne("imsJobTitle", ls, Operator.IN);
        Map<String, Object> map2 = MakeConditionUtil.parseOne("imsGroupId", groupId, Operator.EQUAL);
        List result = new ArrayList();
        result.add(map1);
        result.add(map2);
        List admins = baseService.findAll(FindEntity.newInstance().makeEntityName("imsUserGroup").makeData(MakeConditionUtil.parseLast(result)), new ConditionEntity());
        return admins;
    }

    //审批进群的申请
    public void judeGroup(Map<String, Object> mapData) {
        Map group = new HashMap();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        group.put("imsGroupId", mapData.get("imsGroupId"));
        group.put("imsUserLoginId", mapData.get("userLoginInfoId"));
        group.put("imsAskReplyTime", dateFormat.format(new Date()));
        group.put("imsAskStatus", mapData.get("preImsAskStatus"));
        baseService.update(FindEntity.newInstance().makeEntityName("imsAskGroup").makeData(group));
        if ("agree".equals(mapData.get("preImsAskStatus"))) {
            //插入到群组用户表中
            Map userGroup = new HashMap();
            userGroup.put("imsGroupId", mapData.get("imsGroupId"));
            userGroup.put("imsUserLoginId", mapData.get("userLoginInfoId"));
            userGroup.put("imsJobTitle", "normal");
            userGroup.put("imsUserGroupCreateTime", dateFormat.format(new Date()));
            baseService.insert(FindEntity.newInstance().makeEntityName("imsUserGroup").makeData(userGroup));
        }
    }

    //处理好友申请
    public void judeFriend(Map<String, Object> mapData) {
        Map friend = new HashMap();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String curDate = dateFormat.format(new Date());
        friend.put("imsSendUserId", mapData.get("imsSendUserId"));
        friend.put("imsReceivedId", mapData.get("imsReceivedId"));
        friend.put("imsAskReplyTime", curDate);
        friend.put("imsAskStatus", mapData.get("preImsAskStatus"));
        baseService.update(FindEntity.newInstance().makeEntityName("imsAskFriend").makeData(friend));
        if ("agree".equals(mapData.get("preImsAskStatus"))) {
            //把数据插入到好友关系表中
            Map map1 = new HashMap();
            map1.put("imsFriendId", mapData.get("imsSendUserId"));
            map1.put("imsUserLoginId", mapData.get("imsReceivedId"));
            map1.put("imsUserFriendCreateTime", curDate);
            map1.put("imsFriendAlias", mapData.get("sendUserName"));
            Map map2 = new HashMap();
            map2.put("imsUserLoginId", mapData.get("imsSendUserId"));
            map2.put("imsFriendId", mapData.get("imsReceivedId"));
            map2.put("imsUserFriendCreateTime", curDate);
            map2.put("imsFriendAlias", mapData.get("receiveUserName"));
            baseService.store(FindEntity.newInstance().makeEntityName("imsUserFriend").makeData(map1));
            baseService.store(FindEntity.newInstance().makeEntityName("imsUserFriend").makeData(map2));
        }
    }

    //删除好友
    public void delFriend(Map<String, Object> mapData) {
        Map map1 = new HashMap();
        map1.put("imsFriendId", mapData.get("imsUserLoginId"));
        map1.put("imsUserLoginId", mapData.get("imsFriendId"));
        Map map2 = new HashMap();
        map2.put("imsFriendId", mapData.get("imsFriendId"));
        map2.put("imsUserLoginId", mapData.get("imsUserLoginId"));
        List ls = new ArrayList();
        ls.add(map1);
        ls.add(map2);
        baseService.delSelect("imsUserFriend", ls);
        delChat(mapData.get("imsUserLoginId"), mapData.get("imsFriendId"));
    }

    public void delUserGroup(Map<String, Object> mapData) {
        Map map1 = new HashMap();
        map1.put("imsGroupId", mapData.get("imsGroupId"));
        map1.put("imsUserLoginId", mapData.get("userLoginInfoId"));
        baseService.delete("imsUserGroup", map1);
        delChat(mapData.get("imsGroupId"), mapData.get("userLoginInfoId"));
    }

    public void addGroup(Map<String, Object> mapData) {
        String format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        Map insert = CopyProperties.copyMapData("imsGroup", mapData);
        insert.put("imsGroupCreateTime", format);
        FindEntity findEntity = FindEntity.newInstance().makeEntityName("imsGroup").makeData(insert);
        Map returnGroup = baseService.insert(findEntity);
        //继续绑定用户和群组的关系
        Map relation = new HashMap();
        relation.put("imsUserLoginId", mapData.get("imsUserLoginId"));
        relation.put("imsGroupId", returnGroup.get("imsGroupId"));
        relation.put("imsJobTitle", "creator");
        relation.put("imsUserGroupCreateTime", format);
        baseService.insert(FindEntity.newInstance().makeEntityName("imsUserGroup").makeData(relation));
    }

    private void delChat(Object userId, Object receiveId) {
        //删除两者的会话
        Map map3 = new HashMap();
        map3.put("imsChatSendUserLoginId", userId);
        map3.put("imsChatToReceiveId", receiveId);
        Map map4 = new HashMap();
        map4.put("imsChatSendUserLoginId", receiveId);
        map4.put("imsChatToReceiveId", userId);
        List ls1 = new ArrayList();
        ls1.add(map3);
        ls1.add(map4);
        baseService.delSelect("imsChat", ls1);
    }
}
