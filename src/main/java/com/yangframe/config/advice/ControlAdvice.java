package com.yangframe.config.advice;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @autor 杨瑞
 * @date 2019/5/11 17:30
 */
@ControllerAdvice
public class ControlAdvice {

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public ExceptionEntity handleValidationBodyException(Exception e) {
        e.printStackTrace();
        if(e instanceof BaseException){
            return ((BaseException)e).getExceptionEntity();
        }
        return new ExceptionEntity(500, "服务器内部错误");
    }
}
