package com.demo;

import com.DemoApplication;
import com.demo.chat.service.ChatService;
import com.demo.config.listener.ChatEvent;
import com.demo.config.listener.ChatPublisher;
import com.demo.config.properties.UploadFileProperties;
import com.demo.web.core.crud.service.BaseServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		List list = chatService.getChatMessage("2");
		System.out.println(list);
		Map map=new HashMap();
		map.put("userId", "2");
		map.put("destId", "88");
		map.put("start",1 );
		map.put("end", 10);
		List historyMsg = chatService.getHistoryMsg(map);
		System.out.println(historyMsg);
	}
}
