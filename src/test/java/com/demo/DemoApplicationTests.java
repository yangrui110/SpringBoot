package com.demo;

import com.DemoApplication;
import com.demo.chat.service.ChatService;
import com.demo.config.listener.ChatEvent;
import com.demo.config.listener.ChatPublisher;
import com.demo.config.properties.UploadFileProperties;
import com.demo.config.util.MapUtil;
import com.demo.web.core.crud.centity.CombineOperator;
import com.demo.web.core.crud.centity.ConditionEntity;
import com.demo.web.core.crud.centity.FindEntity;
import com.demo.web.core.crud.centity.Operator;
import com.demo.web.core.crud.service.BaseServiceImpl;
import com.demo.web.core.util.MakeConditionUtil;
import com.demo.web.core.xmlEntity.ColumnProperty;
import com.demo.web.core.xmlEntity.EntityMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;
import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DemoApplication.class)
public class DemoApplicationTests {

	Logger logger=LoggerFactory.getLogger(getClass());

	@Autowired
	private UploadFileProperties properties;

	@Autowired
	private BaseServiceImpl baseService;

	@Autowired
	private ChatService chatService;
		@Test
	public void contextLoads() {

	}

	@Test
	public void test5(){
			Map map=new HashMap();
			map.put("user_id","10001");
			Map map2=new HashMap();
			map2.put("user_password","1234");
		 //Map con=MakeConditionUtil.makeCondition(map);
        Map condition = MakeConditionUtil.makeCondition(map, map2, CombineOperator.OR);
        FindEntity findEntity=new FindEntity();
		findEntity.setCondition(condition);
		findEntity.setEntityName("userLogin");
		 Object o=baseService.findAll(findEntity,new ConditionEntity());
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
	public void test6(){
			List<Map<String,Object>> ls=new ArrayList<>();
		Map map = MapUtil.toMap("teacherId", "10009");
		map.put("userPassword", "12345");
		Map map1 = MapUtil.toMap("teacherId", "10003");
		//map1.put("teacher_pros", "119");
		//map1.put("teacher_name", "ik9");
		ls.add(map);
		ls.add(map1);
		FindEntity entity=new FindEntity();
		entity.setEntityName("TeacherUserView");
		entity.setData(MakeConditionUtil.makeCondition(map));
		//entity.setCondition(MapUtil.toMap("teacher_id", "10002"));
		//Map<String, ColumnProperty> userView = EntityMap.getPrimaryKey("TeacherUserView");
		//
		Map map2 =new HashMap();
		map2.put("teacherId", "10010");
		map2.put("teacherName", "ik");
		map2.put("teacherPros", "113");
		Map map3 =new HashMap();
		map3.put("teacherId", "10011");
		map3.put("teacherName", "lp");
		map3.put("teacherPros", "112");
		Map map4 =new HashMap();
		map4.put("teacherId", "10012");
		map4.put("teacherName", "nk");
		map4.put("teacherPros", "113");
		List<Map<String,Object>> lsOne=new ArrayList<>();
		lsOne.add(map2);
		lsOne.add(map3);
		lsOne.add(map4);
		//baseService.insertAll("Teacher", lsOne);
		baseService.updateAll("Teacher", lsOne);
		//baseService.delSelect("Teacher", lsOne);
		//baseService.findAll(entity,new ConditionEntity());
		//baseService.findByPK("Teacher", map);
	}
}
