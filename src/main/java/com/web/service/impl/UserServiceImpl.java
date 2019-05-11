package com.web.service.impl;

import com.web.dao.UserDao;
import com.web.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @autor 杨瑞
 * @date 2019/5/11 15:14
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Override
    public List finds(Map map) {
        return userDao.finds();
    }
}
