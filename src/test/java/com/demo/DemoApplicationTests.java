package com.demo;

import com.DemoApplication;
import com.demo.web.core.crud.centity.CEntity;
import com.demo.web.core.crud.centity.COrderBy;
import com.demo.web.core.crud.centity.ConditionEntity;
import com.demo.web.core.crud.mysql.service.MysqlAjaxCrudService;
import com.demo.web.core.crud.sql.dao.SqlAjaxCrudDao;
import com.demo.web.core.crud.sql.service.SqlAjaxCrudService;
import com.demo.web.dao.mysql.ZhiyuDao;
import com.demo.web.dao.oracle.OUserDao;
import org.dom4j.DocumentException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DemoApplication.class)
public class DemoApplicationTests {

	Logger logger=LoggerFactory.getLogger(getClass());

	@Autowired
	private ZhiyuDao zhiyuDao;

	@Autowired
	private SqlAjaxCrudService sqlAjaxCrudService;

	@Autowired
	private MysqlAjaxCrudService ajaxCrudService;

	@Test
	public void contextLoads() {

	}

	@Test
	public void test2() throws IOException, DocumentException {
		Map map=new HashMap<>();
		map.put("name", "zhanghan");
		map.put("id", "4");
		map.put("age", "90");
		List list=new ArrayList();
		CEntity cEntity=new CEntity();
		cEntity.setLeft("id");
		//cEntity.setOperator("=");
		cEntity.setRight("2");
		list.add(cEntity);
		COrderBy cOrderBy=new COrderBy();
		cOrderBy.setDirect("desc");
		cOrderBy.setNames("id");
		COrderBy cOrderBy1=new COrderBy();
		cOrderBy1.setDirect("desc");
		cOrderBy1.setNames("name");
		List<COrderBy> orderByList=new ArrayList<>();
		orderByList.add(cOrderBy);
		orderByList.add(cOrderBy1);
		//ajaxCrudService.update("user", map, map2);
		//ajaxCrudService.delete("user", map);
		List results=sqlAjaxCrudService.findAll("Teacher",list ,0 ,10 ,orderByList,new ConditionEntity());
		results.forEach((r)->{
			System.out.println(r);
		});
		//ajaxCrudService.insert("user", map);
	}

	/**
	 *
	 * */

	@Test
	public void test3(){

		COrderBy orderBy=new COrderBy();
		orderBy.setNames("tId");
		orderBy.setDirect("desc");
		COrderBy orderBy1=new COrderBy();
		orderBy1.setNames("cName");
		orderBy1.setDirect("desc");

		List<COrderBy> list=new ArrayList<>();
		list.add(orderBy);
		list.add(orderBy1);

		List<CEntity> lists=new ArrayList<>();
		CEntity entity=new CEntity();
		entity.setLeft("cName");
		entity.setRight("课程4班");
		lists.add(entity);
		Map map=new HashMap();
		map.put("cName", "课程4班");
		map.put("age", "201");
		map.put("id", "10");
		//sqlAjaxCrudService.delete("userOne",lists);
		//System.out.println("插入成功");
		List user = sqlAjaxCrudService.findAll("ClassesView",lists, 0, 10, list,new ConditionEntity());
		user.forEach((k)->{
			System.out.println(k);
		});
	}

}
