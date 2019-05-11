package com.web.test;

import com.web.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;

/**
 * @autor 杨瑞
 * @date 2019/5/11 13:52
 */
@Controller
@RequestMapping("localTest")
public class LocalTestController {

    @Autowired
    private UserService userService;

    @ResponseBody
    @GetMapping("ok")
    public Object ok(){
        return userService.finds(new HashMap());
    }
}
