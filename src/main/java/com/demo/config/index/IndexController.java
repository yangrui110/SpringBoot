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
public class IndexController {

    @Autowired
    private BaseServiceImpl baseService;

    @GetMapping("/")
    public String indexOne(){
        System.out.println(1);
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

        FindEntity findEntity = FindEntity.newInstance().makeEntityName("userLoginView").makeCondition(condition);
        Object all = baseService.findAllNoPage(findEntity, new ConditionEntity());
        if(((List)all).size()<=0){
            throw new BaseException(304, "用户名或者密码不正确");
        }else if(((List)all).size()>1){
            throw new BaseException(305, "该用户在系统中存在重复值");
        }
        Map user= (Map) ((List)all).get(0);
        if("2".equals(user.get("disabled")))
            throw new BaseException(306, "账号被禁用，解封日期："+user.get("disabled_end_time"));
        //获取用户权限信息
        Map instance = MapUtil.newInstance();
        instance.put("userId", userId);
        FindEntity permissions = FindEntity.newInstance().makeEntityName("userPermissions").makeCondition(instance);
        FindEntity roles = FindEntity.newInstance().makeEntityName("userRoleView").makeCondition(instance);
        user.put("permissions", baseService.findAllNoPage(permissions, new ConditionEntity()));
        user.put("roles", baseService.findAllNoPage(roles, new ConditionEntity()));
        request.getSession().setAttribute("userLogin", user);
        return new ResultEntity(ResultEnum.OK,user);
    }

}

