package com.yangframe.config.index;

import com.yangframe.config.advice.BaseException;
import com.yangframe.config.advice.ResultEntity;
import com.yangframe.config.advice.ResultEnum;
import com.yangframe.config.interceptor.LoginDisabled;
import com.yangframe.config.interceptor.LoginSessionAttribute;
import com.yangframe.config.util.MapUtil;
import com.yangframe.config.util.Util;
import com.yangframe.web.core.crud.centity.ConditionEntity;
import com.yangframe.web.core.crud.centity.FindEntity;
import com.yangframe.web.core.crud.service.BaseServiceImpl;
import com.yangframe.web.core.util.MakeConditionUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

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
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private HttpServletRequest request;
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

        FindEntity findEntity = FindEntity.newInstance().makeEntityName("userLoginView").makeData(MakeConditionUtil.makeCondition(condition));
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
        FindEntity permissions = FindEntity.newInstance().makeEntityName("userPermissionsView").makeCondition(instance);
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

    @ApiOperation("获取邮箱验证码")
    @GetMapping("sendMail")
    @ResponseBody
    public ResultEntity sendMail(@RequestParam  String toEmail) throws Exception {
        String code = Util.getRand(4);
        Map<String,Object> codeMap = new HashMap<>();
        System.out.println("验证码："+code);
        codeMap.put("code",code);
        codeMap.put("startDate",new Date());
        codeMap.put("toEmail",toEmail);
        //2.检测该邮箱是否已经被注册
        Map map1 = MapUtil.toMap("userLoginId", toEmail);
        List<Map<String, Object>> allNoPage = baseService.findAllNoPage(FindEntity.newInstance().makeEntityName("UserLogin").makeData(MakeConditionUtil.makeCondition(map1)), new ConditionEntity());
        if(allNoPage.size()>0)
            throw new BaseException(509,"该邮箱已被注册，请直接登录");
        System.out.println("会话ID："+request.getSession().getId());
        request.getSession().setAttribute("codeMap",codeMap);
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
        mimeMessageHelper.setSubject("验证码");
        mimeMessageHelper.setText("您好，您的验证码为："+code+",该验证码，五分钟之内有效！");
        mimeMessageHelper.addTo(toEmail);
        mimeMessageHelper.setFrom("1341672184@qq.com","聊天网");
        mailSender.send(mimeMessage);
        return new ResultEntity(ResultEnum.OK,MapUtil.toMap("result",true));
    }

    @ApiOperation("注册新用户")
    @PostMapping("register")
    @ResponseBody
    public ResultEntity register(@RequestBody Map<String,String> mapData){
        String email=mapData.get("email");
        String password=mapData.get("password");
        String code=mapData.get("code");
        //1.验证邮箱验证码
        Object codeMap = request.getSession().getAttribute("codeMap");
        System.out.println("会话ID："+request.getSession().getId());
        if(codeMap==null)
            throw new BaseException(505,"会话已经过期，请刷新界面");
        Map map = (Map) codeMap;
        //检测是否超过5分钟
        long diff = new Date().getTime() - ((Date) map.get("startDate")).getTime();
        if(diff>5*60*1000){
            throw new BaseException(506,"邮箱验证码已经过期，请重新获取");
        }
        if(!map.get("code").equals(code)){
            throw new BaseException(507,"邮箱验证码输入不正确，请重新输入");
        }
        if(!map.get("toEmail").equals(email))
            throw new BaseException(508,"邮箱和获取验证码的邮箱不一致");
        Map toMap = MapUtil.toMap("userLoginId",email);
        toMap.put("userLoginPassword",password);
        baseService.insert(FindEntity.newInstance().makeEntityName("UserLogin").makeData(toMap));
        baseService.store(FindEntity.newInstance().makeEntityName("UserLoginInfo").makeData(MapUtil.toMap("userLoginInfoId",email)));
        return new ResultEntity(ResultEnum.OK,MapUtil.toMap("result",true));
    }

}

