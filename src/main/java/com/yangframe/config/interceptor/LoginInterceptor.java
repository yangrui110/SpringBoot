package com.yangframe.config.interceptor;

import com.yangframe.config.advice.BaseException;
import com.yangframe.config.advice.ResultEnum;
import com.yangframe.config.util.IpUtils;
import com.yangframe.config.whiteList.WhiteList;
import com.yangframe.web.core.crud.centity.ConditionEntity;
import com.yangframe.web.core.crud.centity.FindEntity;
import com.yangframe.web.core.crud.service.BaseServiceImpl;
import com.yangframe.web.util.file.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @autor 杨瑞
 * @date 2019/6/16 0:25
 */
@Component
public class LoginInterceptor  implements HandlerInterceptor {

    @Autowired
    private BaseServiceImpl baseService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        List<Map<String, Object>> ipBlackList = baseService.findAllNoPage(FindEntity.newInstance().makeEntityName("ipBlackList"), new ConditionEntity());
        List<Map<String, Object>> pathWhiteList = baseService.findAllNoPage(FindEntity.newInstance().makeEntityName("pathWhiteList"), new ConditionEntity());
        //转换成List<String>

        List<String> pathWhiteLists = parseToStringList(pathWhiteList,"path");
        List<String> ipWhiteLists = parseToStringList(ipBlackList,"ip");
        //
        System.out.println("path="+request.getServletPath()+"------IP:"+request.getRemoteAddr()+"---"+ IpUtils.getIpAddr(request));
        //如果IP是在黑名单中
        if(!checkIpIsOk(IpUtils.getIpAddr(request),ipWhiteLists)){
            throw new BaseException(ResultEnum.IP_REJECTED);
        }
        //检测路径的白名单
        if(checkIsOk(request.getServletPath(),pathWhiteLists))
            return true;

        Object userLogin = request.getSession().getAttribute("userLogin");
        if(userLogin==null){
            throw new BaseException(ResultEnum.USER_NOT_LOGIN);
        }
        return true;
    }

    private List<String> parseToStringList(List<Map<String,Object>> mapDatas,String key){
        List<String> list =new ArrayList<>();
        for(Map<String,Object> one: mapDatas){
            list.add((String) one.get(key));
        }
        return list;
    }
    private boolean checkIpIsOk(String ip,List<String> ips){
        for(String one :ips){
            if(ip.equals(one)){
                return false;
            }
        }
        return true;
    }
    private boolean checkIsOk(String path,List<String> paths){
        for(String one : paths){
            //检查是否是以/**结尾
            if(one.endsWith("/**")){
                //检测path是否以one开头
                if(path.startsWith(one.replace("/**",""))){
                    return true;
                }
            }else if(one.endsWith("/*")){
                String replace = one.replace("/*", "");
                if(path.startsWith(replace)){
                    //继续检测，是否是只有一个下级路径
                    String right = path.replace(replace, "");
                    if(right.lastIndexOf("/")==0){
                        return true;
                    }
                }
            }else if(path.equals(one)) {
                return true;
            }
        }
        return false;
    }

}