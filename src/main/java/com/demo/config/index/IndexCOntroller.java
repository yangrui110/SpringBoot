package com.demo.config.index;

import com.demo.config.advice.BaseException;
import com.demo.config.advice.ResultEntity;
import com.demo.config.advice.ResultEnum;
import com.demo.config.util.MapUtil;
import com.demo.web.core.crud.centity.ConditionEntity;
import com.demo.web.core.crud.centity.FindEntity;
import com.demo.web.core.crud.service.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @autor 杨瑞
 * @date 2019/5/14 16:17
 */
@Controller
@RequestMapping("")
public class IndexCOntroller {

    @Autowired
    private BaseServiceImpl baseService;

    @GetMapping("/")
    public String indexOne(){
        System.out.println(12333);
        return "index";
    }

    @ResponseBody
    @PostMapping("login")
    public ResultEntity login(@RequestBody Map map, HttpServletRequest request){
        Object userId = map.get("userName");
        Object password = map.get("password");
        if(userId==null||password==null){
            throw new BaseException(304,"用户名或者密码为空");
        }
        Map condition = MapUtil.toMap("userName", userId);
        condition.put("login_password", password);

        FindEntity findEntity = FindEntity.newInstance().makeEntityName("userLoginView").makeCondition(condition).makeStart(1).makeEnd(10);
        Object all = baseService.findAll(findEntity, new ConditionEntity());
        if(((List)all).size()<=0){
            throw new BaseException(304, "用户名或者密码不正确");
        }
        Map user= (Map) ((List)all).get(0);
        request.getSession().setAttribute("userLogin", user);
        return new ResultEntity(ResultEnum.OK,user);
    }

}

