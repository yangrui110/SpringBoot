package com.demo;

import com.DemoApplication;
import com.demo.wanxidi.dao.WanRelationDao;
import com.demo.web.core.crud.service.BaseServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DemoApplication.class)
public class DemoApplicationTests {

	Logger logger=LoggerFactory.getLogger(getClass());

	@Autowired
	private BaseServiceImpl baseService;

	@Autowired
	private WanRelationDao wanRelationDao;
		@Test
	public void contextLoads() {

	}

	@Test
	public void test5(){
		Map map =new HashMap();
		map.put("id", "1");
		List relation = wanRelationDao.findRelation(map, 0, 10);

		int relation1 = wanRelationDao.countRelation(map);
		System.out.println(relation);
		System.out.println("数目："+relation1);
	}
}
