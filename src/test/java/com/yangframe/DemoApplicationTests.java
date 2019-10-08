package com.yangframe;

import com.DemoApplication;
import com.yangframe.chat.service.ChatService;
import com.yangframe.config.properties.UploadFileProperties;
import com.yangframe.config.util.MapUtil;
import com.yangframe.web.core.crud.centity.CombineOperator;
import com.yangframe.web.core.crud.centity.ConditionEntity;
import com.yangframe.web.core.crud.centity.FindEntity;
import com.yangframe.web.core.crud.service.BaseServiceImpl;
import com.yangframe.web.core.crud.service.BaseServiceExternal;
import com.yangframe.web.core.util.MakeConditionUtil;
import com.yangframe.web.core.xmlEntity.ColumnProperty;
import com.yangframe.web.core.xmlEntity.EntityMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DemoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DemoApplicationTests {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private UploadFileProperties properties;

    @Autowired
    private BaseServiceImpl baseService;

    @Autowired
    private BaseServiceExternal baseServiceInner;
    @Autowired
    private ChatService chatService;

    @Test
    public void contextLoads() {

    }

    @Test
    public void test5() {
        Map map = new HashMap();
        map.put("user_id", "10001");
        Map map2 = new HashMap();
        map2.put("user_password", "1234");
        //Map con=MakeConditionUtil.makeCondition(map);
        Map condition = MakeConditionUtil.makeCondition(map, map2, CombineOperator.OR);
        FindEntity findEntity = new FindEntity();
        findEntity.setCondition(condition);
        findEntity.setEntityName("userLogin");
        Object o = baseService.findAll(findEntity, new ConditionEntity());
        System.out.println(o);
		/*ChatEvent event=new ChatEvent("chat", "哈哈哈哈");
		ChatPublisher.publishEvent(event);*/
		/*Map<String,Object> map=new HashMap<>();
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		map.put("userId", "2");
		map.put("destId", "88");
		map.put("type", "qz");
		map.put("content", "具体呢荣有");
		map.put("createTime", dateFormat.format(new Date()));
		chatService.sendMsg(map);*/
		/*List list = chatService.getChatMessage("2");
		System.out.println(list);
		Map map=new HashMap();
		map.put("userId", "2");
		map.put("destId", "88");
		map.put("start",1 );
		map.put("end", 10);
		List historyMsg = chatService.getHistoryMsg(map);
		System.out.println(historyMsg);*/
    }

    @Test
    public void test6() {
        List<Map<String, Object>> ls = new ArrayList<>();
        Map map = MapUtil.toMap("teacherID", "10009");
        map.put("userPassword", "12345");
        Map map1 = MapUtil.toMap("teacherId", "10003");
        //map1.put("teacher_pros", "119");
        //map1.put("teacher_name", "ik9");
        ls.add(map);
        ls.add(map1);
        FindEntity entity = new FindEntity();
        entity.setEntityName("TeacherUserView");
        entity.setData(MakeConditionUtil.makeCondition(map));
        //entity.setCondition(MapUtil.toMap("teacher_id", "10002"));
        //Map<String, ColumnProperty> userView = EntityMap.getPrimaryKey("TeacherUserView");
        //
        Map map2 = new HashMap();
        map2.put("teacherId", "10010");
        map2.put("teacherName", "ik");
        map2.put("teacherPros", "113");
        Map map3 = new HashMap();
        map3.put("teacherId", "10011");
        map3.put("teacherName", "lp");
        map3.put("teacherPros", "112");
        Map map4 = new HashMap();
        map4.put("teacherId", "10012");
        map4.put("teacherName", "nk");
        map4.put("teacherPros", "113");
        List<Map<String, Object>> lsOne = new ArrayList<>();
        lsOne.add(map2);
        lsOne.add(map3);
        lsOne.add(map4);
        Map<String, ColumnProperty> columns = EntityMap.getNoExcludeColumns("TeacherUserView");
        System.out.println(11);
        //baseService.insertAll("Teacher", lsOne);
        //baseService.updateAll("Teacher", lsOne);
        //baseService.delSelect("Teacher", lsOne);
        //baseService.findAll(entity,new ConditionEntity());
        //baseService.findByPK("Teacher", map);
    }

    @Test
    public void test8() {
        Map one = new HashMap();
        one.put("imsGroupName", "哈哈3号");
        one.put("imsGroupLabel", "请问您有什么意见？");
        Map two = new HashMap();
        two.put("imsGroupId", "100002");
        two.put("imsGroupName", "哈哈4号");
        two.put("imsGroupLabel", "大胆！！！！");
        Map three = new HashMap();
		three.put("imsGroupName", "哈哈5号");
		three.put("imsGroupLabel", "请问您有什么意见？");
        List list = new ArrayList();
        list.add(one);
        list.add(two);
        list.add(three);
        baseServiceInner.storeAll("imsGroup",list);
    }

    @Test
    public void test7() {
        String entityName = "userRole";
        Map<String, Object> data1 = MapUtil.toMap("userLoginId", "111");
        data1.put("roleId", "1000");
        data1.put("userRoleCreateTime", "2019-01-02 09:00:00");
        Map<String, Object> data2 = MapUtil.toMap("userLoginId", "111");
        data2.put("roleId", "1004");
        List ls = new ArrayList();
        ls.add(data1);
        ls.add(data2);
        List<String> mm = new ArrayList<>();
        mm.add("userLoginId");
        baseServiceInner.updateManyToManyTable(entityName, ls, mm);
    }
}
