package com.web.auction.service.impl;

import com.web.auction.mapper.UserMapper;
import com.web.auction.pojo.User;
import com.web.auction.pojo.UserExample;
import com.web.auction.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.List;


@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    //注销
    @Override
    public void dologout(HttpSession session) {
        session.invalidate();

    }

    @Override
    public User login(String username, String userpassword) {

        UserExample example = new UserExample();

        //账号和密码作为拼接条件
        UserExample.Criteria criteria = example.createCriteria();
        criteria.andUsernameEqualTo(username);
        criteria.andUserpasswordEqualTo(userpassword);

        //根据实例查询用户
        List<User> list = userMapper.selectByExample(example);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }

        return null;
    }

    @Override
    public User loginByUsername(String username) {

        UserExample example = new UserExample();
        UserExample.Criteria criteria = example.createCriteria();
        criteria.andUsernameEqualTo(username);
        List<User> userList = userMapper.selectByExample(example);

        if (userList != null && userList.size() > 0) {
            return userList.get(0);
        }
        return null;
    }

    @Override
    public void register(User user) {

        userMapper.insert(user);
    }

    //配置事务环境
    @Transactional
    @Override
    public boolean checkUser(String username) {
        UserExample example = new UserExample();

        UserExample.Criteria criteria = example.createCriteria();
        criteria.andUsernameEqualTo(username);

        List<User> list = userMapper.selectByExample(example);
        if (list != null && list.size() > 0) {

            return true;
        }
        return false;
    }

}
