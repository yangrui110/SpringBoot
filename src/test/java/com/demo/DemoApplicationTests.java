package com.demo;

import com.DemoApplication;
import com.demo.web.core.crud.service.BaseServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DemoApplication.class)
public class DemoApplicationTests {

	Logger logger=LoggerFactory.getLogger(getClass());

	@Autowired
	private BaseServiceImpl baseService;
		@Test
	public void contextLoads() {

	}

	@Test
	public void test5(){
		System.out.println(-1&"666".hashCode());
	}
}
