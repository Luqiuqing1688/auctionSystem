package com.web.auction.interceptor;

import com.web.auction.pojo.User;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

//创建对象并由Spring容器托管
@Component
public class CheckUserInterceptor implements HandlerInterceptor {

    // 调用Handler之前拦截
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object arg2) throws Exception {


        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("loginUser");
        // 3.判断用户是否登录
        if (user != null) {
            return true;
        } else {

            response.sendRedirect(request.getContextPath() + "/login.html");

            return false;
        }

    }

    // 调用完Handler核心程序，返回ModelAndView之前
    @Override
    public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, ModelAndView arg3)
            throws Exception {

    }

    // 执行完Handler之后调用
    @Override
    public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3)
            throws Exception {

    }

}
