package com.demo.chat.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.demo.config.advice.BaseException;
import com.demo.config.util.JedisUtil;
import com.demo.config.util.MapUtil;
import com.demo.web.core.crud.centity.ConditionEntity;
import com.demo.web.core.crud.centity.FindEntity;
import com.demo.web.core.crud.service.BaseServiceImpl;
import com.demo.web.core.util.FindEntityUtil;
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
     * 获取首页聊天列表步骤：
     * 1.获取数据库中的某个用户的关联关系表
     * 2.从Redis中获取每个记录对应的未读消息数目，以及名称chat_userId_groupId
     * */
    public List getChatMessage(String userId){
        FindEntity entity=new FindEntity();
        entity.setCondition(MapUtil.toMap("user_id", userId));
        entity.setEntityName("userFriendView");
        List<Map<String, Object>> list = baseService.findAllNoPage(entity, new ConditionEntity());
        List<Map<String,Object>> result=new ArrayList<>();  //存放结果值，每个记录的最新结果
        for(Map<String,Object> map:list){
            String referId = (String) map.get("refer_id");
            String type = (String) map.get("refer_type");
            String refer = JedisUtil.getByKey("chat_" + userId + "_" +referId );
            int notRead=0;
            if(refer!=null){
                JSONObject jsonObject = JSON.parseObject(refer);
                notRead=jsonObject.getInteger("notRead"); //未读消息数量
            }

            if("yh".equals(type)) {
                map.put("icon", map.get("userIcon"));
                map.put("name", map.get("userName"));
            } else if("qz".equals(type)) {
                map.put("icon", map.get("groupIcon"));
                map.put("name", map.get("groupName"));
            }else {
                map.put("icon","");
                map.put("name", "");
            }
            map.put("notRead", notRead);
            result.add(map);
        }
        return result;
    }
    
    /**
     * 获取历史消息
     * @param map json样式：{userId:"",destId:'',start:1,end:10}
     * */
    public List getHistoryMsg(Map map){
        Object userId = map.get("userId");
        Object destId = map.get("destId");
        Integer start = (Integer) map.get("start");
        Integer end = (Integer) map.get("end");
        String byKey = JedisUtil.getByKey("chat_" + userId + "_" + destId);
        List result = new JSONArray();
        if(!StringUtils.isEmpty(byKey)){
            JSONObject jsonObject = JSONObject.parseObject(byKey);
            List data = (List) jsonObject.get("data");

            if(start*end>=data.size()&&(start-1)*end<data.size()) {
               //result=Arrays.copyOfRange(data, (start-1)*end,  data.size() - (start - 1) * end);
                //System.arraycopy(data, (start-1)*end, result, 0, data.size() - (start - 1) * end);
                //剪切掉分页的数据,从后往前剪切
                int s=data.size()-(start-1)*end-1;
                //长度
                int len=data.size()-(start*end>data.size()?data.size():start*end);
                for(int i=s;i>len;i--){
                    result.add(data.get(i));
                }
            }
        }
        return result;
    }
    /**
     * 发送消息
     * @param maps
     * */
    public void sendMsg(Map<String,Object> maps){
        String userId = (String) maps.get("userId");
        String destId = (String) maps.get("destId");
        String content= (String) maps.get("content");
        String createTime= (String) maps.get("createTime");
        String type = (String) maps.get("type"); //消息的类型,分为qz,hy
        String userKey = JedisUtil.getByKey("chat_" + userId+"_"+destId);
        if(StringUtils.isEmpty(userKey)) {
            Map instance = MapUtil.newInstance();
            List data = new ArrayList();
            data.add(maps);
            instance.put("notRead", 0);
            instance.put("data", data);
            JedisUtil.set("chat_" + userId+"_"+destId, JSON.toJSONString(instance));
        }else{
            JSONObject jsonObject = JSON.parseObject(userKey);
            List data = (List) jsonObject.get("data");
            data.add(maps);
            Integer notRead = jsonObject.getInteger("notRead");
            notRead++;
            jsonObject.put("notRead", notRead);
            jsonObject.put("data", data);
            JedisUtil.set("chat_" + userId+"_"+destId, JSON.toJSONString(jsonObject));
        }
        //更新最后一条记录值
        Map map=new HashMap();
        map.put("user_id", userId);
        map.put("refer_id", destId);
        //构造待更新的条件
        Map data=new HashMap();
        data.put("refer_type", type);
        data.put("last_content", content);
        data.put("last_msg_time", createTime);
        data.put("user_id", userId);
        data.put("refer_id", destId);
        FindEntity findEntity = new FindEntityUtil().newInstance().makeCondition(map).makeEntityName("userFriendGroup").makeData(data).getFindEntity();
        List<Map<String, Object>> allNoPage = baseService.findAllNoPage(findEntity, new ConditionEntity());
        if(allNoPage.size()==0){
            SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            findEntity.getData().put("create_time", dateFormat.format(new Date()));
            baseService.insert(findEntity);
        }else {
            baseService.update(findEntity);
        }
        //通知前台接收到新消息
    }

    private String setInfo(String userId){

        //从数据库中拿到用户信息，并放入到Redis中
        FindEntityUtil entityUtil = new FindEntityUtil().newInstance().makeCondition(MapUtil.toMap("user_id", userId)).makeEntityName("userLogin");
        List<Map<String, Object>> list = baseService.findAllNoPage(entityUtil.getFindEntity(), new ConditionEntity());
        if(list.size()<=0)
            throw new BaseException(306,"用户Id不合法");
        String userKey=JSON.toJSONString(list.get(0));
        JedisUtil.set("user_"+userId, userKey);
        return userKey;
    }

    /*private String getInfoByType(String destId,String type){
        String referKey="";
        if("qz".equals(type)){
            referKey = JedisUtil.getByKey("group_" + destId);
            if(referKey==null){
                //从数据库中拿到用户信息，并放入到Redis中
                FindEntityUtil entityUtil = new FindEntityUtil().newInstance().makeCondition(MapUtil.toMap("group_id", destId)).makeEntityName("chat_group");
                List<Map<String, Object>> list = baseService.findAllNoPage(entityUtil.getFindEntity(), new ConditionEntity());
                if(list.size()<=0)
                    throw new BaseException(306,"群组不合法");
                referKey=JSON.toJSONString(list.get(0));
                JedisUtil.set("group_"+destId, referKey);
            }
        }else if("hy".equals(type)){
            referKey= JedisUtil.getByKey("user_" + destId);
            if(referKey==null)
                referKey=setInfo(destId);
        }
        return referKey;
    }*/
}
