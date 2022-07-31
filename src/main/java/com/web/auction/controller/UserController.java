package com.web.auction.controller;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.web.auction.pojo.User;
import com.web.auction.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.List;

@Controller
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private DefaultKaptcha captchaProducer;

    @GetMapping("{url}.html")
    public String url(@PathVariable String url) {
        return url;
    }

    //	@RequestMapping("/doLogin")
//	public String doLogin(String username, String password, String valideCode, HttpSession session,Model model) {
//
//		// 1.判断验证码是否正确
//		String numrand = (String) session.getAttribute("vrifyCode");
//		if(!valideCode.equals(numrand)){
//			model.addAttribute("errorMsg", "验证码错误!");
//			return "login";
//		}
//
//		// 2.判断账号和密码
//		User user = userService.login(username, password);
//		if (user != null) {
//			//成功
//			session.setAttribute("loginUser", user);
//			return "redirect:/queryAuctions";
//
//		}else{
//			model.addAttribute("errorMsg", "账号或密码错误");
//			return "login";
//		}
//	}
    //认证失败后的方法
    @RequestMapping("/doLogin")
    public String login(Model model, HttpServletRequest request) {
        String exception = (String) request.getAttribute(FormAuthenticationFilter.DEFAULT_ERROR_KEY_ATTRIBUTE_NAME);
        if (exception != null) {
            if (UnknownAccountException.class.getName().equals(exception)) {
                model.addAttribute("errorMsg", "账号不存在");

            }
            if (IncorrectCredentialsException.class.getName().equals(exception)) {
                model.addAttribute("errorMsg", "密码错误");
            }
            if ("valideCodeError".equals(exception)) {
                model.addAttribute("errorMsg", "验证码错误");
            }
        }
        return "login";
    }

    //认证成功后跳转到该方法
    @RequestMapping("/main")
    public String main(HttpSession session) {
        //提取Shiro上下文用户信息
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        session.setAttribute("loginUser", user);

        return "redirect:/queryAuctions";
    }

    @RequestMapping("/doRegister")
    public String doRegister(
            Model model,
            //@Validated表示在对user参数绑定时进行校验,BindingResult必须跟在校验对象后面
            @ModelAttribute("registerUser") @Validated User user, BindingResult bindingResult) {

        //1.判断表单是否通过验证
        if (bindingResult.hasErrors()) {
            List<FieldError> list = bindingResult.getFieldErrors();
            for (FieldError fieldError : list) {
                model.addAttribute(fieldError.getField(), fieldError.getDefaultMessage());
            }
            return "register";
        }
        //2.添加新用户
        userService.register(user);
        return "login";
    }

//	@RequestMapping("/doLogout")
//	public String doLogout(HttpSession session) {
//
//		userService.dologout(session);
//		return "login";
//	}

    @RequestMapping("/checkUserInfo")
    @ResponseBody
    public String cheakUserInfo(String username) {
        System.out.println(username);
        boolean b = userService.checkUser(username);

        return b + "";
    }


    /**
     * 获取验证码
     *
     * @param httpServletRequest
     * @param httpServletResponse
     * @throws Exception
     */
    @RequestMapping("/defaultKaptcha")
    public void defaultKaptcha(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
            throws Exception {
        byte[] captchaChallengeAsJpeg = null;
        ByteArrayOutputStream jpegOutputStream = new ByteArrayOutputStream();
        try {
            // 生产验证码字符串并保存到session中
            String createText = captchaProducer.createText();
            httpServletRequest.getSession().setAttribute("vrifyCode", createText);
            // 使用生产的验证码字符串返回一个BufferedImage对象并转为byte写入到byte数组中
            BufferedImage challenge = captchaProducer.createImage(createText);
            ImageIO.write(challenge, "jpg", jpegOutputStream);
        } catch (IllegalArgumentException e) {
            httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // 定义response输出类型为image/jpeg类型，使用response输出流输出图片的byte数组
        captchaChallengeAsJpeg = jpegOutputStream.toByteArray();
        httpServletResponse.setHeader("Cache-Control", "no-store");
        httpServletResponse.setHeader("Pragma", "no-cache");
        httpServletResponse.setDateHeader("Expires", 0);
        httpServletResponse.setContentType("image/jpeg");
        ServletOutputStream responseOutputStream = httpServletResponse.getOutputStream();
        responseOutputStream.write(captchaChallengeAsJpeg);
        responseOutputStream.flush();
        responseOutputStream.close();
    }
}
