package com.web.test;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @autor 杨瑞
 * @date 2019/5/11 13:52
 */
@Controller
@RequestMapping("localTest")
public class LocalTestController {

    @ResponseBody
    @GetMapping("ok")
    public String ok(){
        return "ok";
    }
}
