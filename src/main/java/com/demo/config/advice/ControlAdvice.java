package com.demo.config.advice;

/**
 * @autor 杨瑞
 * @date 2019/5/11 17:30
 */
//@ControllerAdvice
public class ControlAdvice {

    //@ResponseBody
    //@ExceptionHandler(Exception.class)
    public ExceptionEntity handleValidationBodyException(Exception e) {
        if(e instanceof BaseException){
            return ((BaseException)e).getExceptionEntity();
        }
        return new ExceptionEntity(500, "服务器内部错误");
    }
}
