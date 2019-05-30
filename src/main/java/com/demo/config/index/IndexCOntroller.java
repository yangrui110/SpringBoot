package com.demo.config.index;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @autor 杨瑞
 * @date 2019/5/14 16:17
 */
@Controller
@RequestMapping("")
public class IndexCOntroller {

    @GetMapping("/")
    public String indexOne(){
        System.out.println(1);
        return "index";
    }

}

