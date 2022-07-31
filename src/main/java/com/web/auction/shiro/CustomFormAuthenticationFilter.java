package com.web.auction.shiro;

import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CustomFormAuthenticationFilter extends FormAuthenticationFilter {


    @Override  // 先校验验证码
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        //生成的随机数
        String validateCode = (String) req.getSession().getAttribute("vrifyCode");
        //用户输入 的验证码
        String randomcode = request.getParameter("valideCode");

        if (validateCode != null && randomcode != null && !validateCode.equals(randomcode)) {
            request.setAttribute(DEFAULT_ERROR_KEY_ATTRIBUTE_NAME, "valideCodeError");
            return true;  //不再 调用Realm  ---> login controller
        }

        return super.onAccessDenied(request, response);  // 执行默认操作  ： 调用realm
    }

    //当成功认证后，但访问的是空白资源的时候，需要重定向到main
    @Override
    protected boolean onLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest request,
                                     ServletResponse response) throws Exception {
        WebUtils.getAndClearSavedRequest(request);
        WebUtils.redirectToSavedRequest(request, response, "/main");
        return false;
    }
}