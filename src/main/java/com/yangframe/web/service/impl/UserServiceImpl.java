package com.yangframe.web.service.impl;

import com.yangframe.web.dao.mysql.UserDao;
import com.yangframe.web.service.UserService;

import java.util.List;
import java.util.Map;

/**
 * @autor 杨瑞
 * @date 2019/5/11 15:14
 */
//@Service
public class UserServiceImpl implements UserService {

    //@Autowired
    private UserDao userDao;

    @Override
    public List finds(Map map) {
        return userDao.finds();
    }
}
