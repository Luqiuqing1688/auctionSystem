package com.web.auction.config;

import com.web.auction.interceptor.CheckUserInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SpringMVCConfig implements WebMvcConfigurer {

    @Autowired
    private CheckUserInterceptor checkUserInterceptor;
    @Autowired
    private FileProperties fileProperties;

    //使用虚拟的路径映射物理的路径
    //addResourceHandler  表示定义虚拟路径 pic
    //addResourceLocations  表示定义物理路径 file:+d:/upload
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("/pic/**").addResourceLocations("file:"+"d:/upload/");
        registry.addResourceHandler(fileProperties.getStaticAccessPath()).addResourceLocations("file:" + fileProperties.getUploadFileFolder());

    }
    //拦截器
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//
//        List<String> path = new ArrayList<>();
//        path.add("/css/**");
//        path.add("/images/**");
//        path.add("/js/**");
//        path.add("/login.html");
//        path.add("/doLogin");
//        path.add("/defaultKaptcha");
////        registry.addInterceptor(new CheckUserInterceptor()).addPathPatterns("/**").excludePathPatterns(path);
//        registry.addInterceptor(checkUserInterceptor).addPathPatterns("/**").excludePathPatterns(path);
//    }
}
