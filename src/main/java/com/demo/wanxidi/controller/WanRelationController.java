package com.demo.wanxidi.controller;

import com.demo.config.advice.ResultEntity;
import com.demo.config.advice.ResultEnum;
import com.demo.wanxidi.dao.WanRelationDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @autor 杨瑞
 * @date 2019/7/8 0:18
 */
@Controller
@RequestMapping("wanRelation")
public class WanRelationController {

    @Autowired
    WanRelationDao wanRelationDao;

    @ResponseBody
    @GetMapping("finds")
    public ResultEntity findRelation(){

        return new ResultEntity(ResultEnum.OK, wanRelationDao.findRelation());
    }

}
