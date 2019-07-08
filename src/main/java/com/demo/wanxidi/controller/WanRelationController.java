package com.demo.wanxidi.controller;

import com.alibaba.fastjson.JSONObject;
import com.demo.config.advice.ResultEntity;
import com.demo.config.advice.ResultEnum;
import com.demo.wanxidi.RelationCondition;
import com.demo.wanxidi.dao.WanRelationDao;
import com.demo.web.util.file.xls.ExcelWriter;
import com.demo.web.util.http.HeaderType;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @autor 杨瑞
 * @date 2019/7/8 0:18
 */
@Controller
@RequestMapping("wanRelation")
public class WanRelationController {

    @Autowired
    WanRelationDao wanRelationDao;

    @ResponseBody
    @PostMapping("findRelation")
    public ResultEntity findRelation(@RequestBody RelationCondition relationCondition){
        List<Map> relation = wanRelationDao.findRelation(relationCondition.getCondition(), relationCondition.getStart(), relationCondition.getEnd());
        int relation1 = wanRelationDao.countRelation(relationCondition.getCondition());
        return new ResultEntity(ResultEnum.OK, relation,relation1);
    }

    @GetMapping("export")
    public Object export(@RequestParam("param")String param,HttpServletResponse response) throws IOException {
       /* ServletInputStream inputStream = request.getInputStream();
        byte[] bys=new byte[1024];
        int len=-1;
        ByteArrayOutputStream out=new ByteArrayOutputStream();
        while ((len=inputStream.read(bys))!=-1){
            out.write(bys, 0, len);
        }
        byte[] bytes = out.toByteArray();
        String s = new String(bytes);
        out.close();*/
        if(!StringUtils.isEmpty(param)) {
            JSONObject jsonObject = JSONObject.parseObject(param);
            List relation = wanRelationDao.findRelation(jsonObject, 0, Integer.MAX_VALUE);
            //下载文件
            Map map=new HashMap();
            map.put("numId", "用户编号");
            map.put("referralCode", "用户推荐码");
            map.put("parentCode", "父级推荐码");
            map.put("userName", "用户名");
            map.put("parentName", "父级用户名");
            map.put("deep", "深度");
            HSSFWorkbook workbook= ExcelWriter.writeIntoOneSheet("用户数据表", relation, map);
            String name="用户数据表.xls";
            HeaderType.setResponseFile(name, response);
            OutputStream os = response.getOutputStream();
            workbook.write(os);
            os.flush();
            os.close();
        }
        return null;
    }

}
