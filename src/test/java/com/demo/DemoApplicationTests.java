package com.demo;

import com.DemoApplication;
import com.demo.web.core.crud.centity.CEntity;
import com.demo.web.core.crud.centity.COrderBy;
import com.demo.web.core.crud.centity.FindEntity;
import com.demo.web.core.crud.service.BaseServiceImpl;
import com.demo.web.dao.mysql.ZhiyuDao;
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
	private BaseServiceImpl baseService;

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
		cEntity.setLeft("tId");
		//cEntity.setOperator("=");
		cEntity.setRight("2");
		list.add(cEntity);
		COrderBy cOrderBy=new COrderBy();
		cOrderBy.setDirect("desc");
		cOrderBy.setNames("tId");
		COrderBy cOrderBy1=new COrderBy();
		cOrderBy1.setDirect("desc");
		cOrderBy1.setNames("sName");
		List<COrderBy> orderByList=new ArrayList<>();
		orderByList.add(cOrderBy);
		orderByList.add(cOrderBy1);
		FindEntity entity=new FindEntity();
		entity.setEntityName("TSView");
		entity.setCondition(list);
		entity.setStart(0);
		entity.setEnd(10);
		entity.setOrderBy(orderByList);
		//ajaxCrudService.update("user", map, map2);
		//ajaxCrudService.delete("user", map);
		/*List results=mysqlAjaxCrudService.findAll(entity,new ConditionEntity());
		results.forEach((r)->{
			System.out.println(r);
		});*/
		FindEntity entity1=new FindEntity();
		Map data =new HashMap<>();
		data.put("name", "zw1");
		CEntity cEntity1=new CEntity();
		cEntity1.setLeft("id");cEntity1.setRight("9");
		List ars=new ArrayList();
		ars.add(cEntity1);
		entity1.setEntityName("Teacher");
		entity1.setData(data);
		entity1.setCondition(ars);
		baseService.update(entity1);
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
	}

	@Test
	public void test4(){
		Map map=new HashMap();
		map.put("name", "呵呵呵哒");
		List<CEntity> entities=new ArrayList<>();
		CEntity entity=new CEntity();
		entity.setLeft("id");
		entity.setRight("1");
		entities.add(entity);
		//baseService.update("Teacher", map, entities);
	}

	@Test
	public void test5(){
		System.out.println(-1&"666".hashCode());
	}
}
