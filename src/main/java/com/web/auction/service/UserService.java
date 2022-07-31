package com.web.auction.service;

import com.web.auction.pojo.User;

import javax.servlet.http.HttpSession;


public interface UserService {

    void dologout(HttpSession session);

    User login(String username, String userpassword);

    User loginByUsername(String username);

    void register(User user);

    boolean checkUser(String username);
}
