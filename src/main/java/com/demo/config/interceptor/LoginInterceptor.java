package com.demo.config.interceptor;

import com.demo.config.advice.BaseException;
import com.demo.config.advice.ResultEnum;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @autor 杨瑞
 * @date 2019/6/16 0:25
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Object userLogin = request.getSession().getAttribute("userLogin");
        if(userLogin==null){
            throw new BaseException(ResultEnum.USER_NOT_LOGIN);
        }
        return true;
    }
}
