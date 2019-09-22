package com.yangframe.config.index;

import com.yangframe.config.advice.BaseException;
import com.yangframe.config.advice.ResultEntity;
import com.yangframe.config.advice.ResultEnum;
import com.yangframe.config.interceptor.LoginDisabled;
import com.yangframe.config.interceptor.LoginSessionAttribute;
import com.yangframe.config.util.MapUtil;
import com.yangframe.web.core.crud.centity.ConditionEntity;
import com.yangframe.web.core.crud.centity.FindEntity;
import com.yangframe.web.core.crud.service.BaseServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @autor 杨瑞
 * @date 2019/5/14 16:17
 */
@Api("基础方法")
@Controller
@RequestMapping("")
public class IndexController {

    @Autowired
    private BaseServiceImpl baseService;

    @GetMapping("/")
    public String indexOne(){
        System.out.println(12333);
        return "index";
    }

    @ApiOperation("登陆系统")
    @ResponseBody
    @PostMapping("login")
    public ResultEntity login(@RequestBody Map map, HttpServletRequest request){
        Object userId = map.get("userName");
        Object password = map.get("password");
        if(userId==null||password==null){
            throw new BaseException(304,"用户名或者密码为空");
        }
        Map condition = MapUtil.toMap("userLoginId", userId);
        condition.put("userLoginPassword", password);

        FindEntity findEntity = FindEntity.newInstance().makeEntityName("UserLogin").makeCondition(condition);
        Object all = baseService.findAllNoPage(findEntity, new ConditionEntity());
        if(((List)all).size()<=0){
            throw new BaseException(304, "用户名或者密码不正确");
        }else if(((List)all).size()>1){
            throw new BaseException(305, "该用户在系统中存在重复值，联系管理员进行解决！");
        }
        Map user= (Map) ((List)all).get(0);
        if(LoginDisabled.DISABLED.equals(user.get("userLoginDisabled")))
            throw new BaseException(306, "账号被禁用，解封日期："+user.get("disabled_end_time"));
        //获取用户权限信息
        Map instance = MapUtil.newInstance();
        instance.put("userLoginId", userId);
        FindEntity permissions = FindEntity.newInstance().makeEntityName("userPermissions").makeCondition(instance);
        FindEntity roles = FindEntity.newInstance().makeEntityName("userRoleView").makeCondition(instance);
        List<Map<String, Object>> pes = baseService.findAllNoPage(permissions, new ConditionEntity());
        List<String> rs=new ArrayList<>();
        for(Map o:pes){
            rs.add((String) o.get("permissionName"));
        }
        List<Map<String, Object>> rols = baseService.findAllNoPage(roles, new ConditionEntity());
        List<String> ros=new ArrayList<>();
        for(Map o:rols){
            ros.add((String) o.get("roleName"));
        }
        //
        LoginSessionAttribute sessionAttribute = new LoginSessionAttribute();
        sessionAttribute.setLoginId((String) userId);
        sessionAttribute.setLoginInfo(user);
        sessionAttribute.setPermissions(rs);
        sessionAttribute.setRoles(ros);
        return new ResultEntity(ResultEnum.OK,user);
    }

}

