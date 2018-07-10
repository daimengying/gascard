package com.lirong.gascard.config;

import com.lirong.gascard.interceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 *  注册自定义拦截器
 *  @author daimengying
 */
@Configuration
public class MvcConfig extends WebMvcConfigurationSupport {
    @Autowired
    LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册监控拦截器
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/","/userManager/*","/toModifyPassword","/modifyPassword","/financialManage/*","/gascardManage/*","/orderManage/*");
//                .addPathPatterns("/*")
//                .excludePathPatterns("/startCaptcha","/loginout","/login","/toLogin","/charge/*","/notify/*");
        super.addInterceptors(registry);
    }

    /**
     * 资源处理器
     * 放行static下的静态资源
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/ace-master/**")
                .addResourceLocations("classpath:/static/ace-master/");
        registry.addResourceHandler("/bootstrap-table/**")
                .addResourceLocations("classpath:/static/bootstrap-table/");
        registry.addResourceHandler("/common/**")
                .addResourceLocations("classpath:/static/common/");
        registry.addResourceHandler("/js/**")
                .addResourceLocations("classpath:/static/js/");
        registry.addResourceHandler("/plugins/**")
                .addResourceLocations("classpath:/static/plugins/");
        registry.addResourceHandler("/favicon.ico")
                .addResourceLocations("classpath:/static/favicon.ico");
        super.addResourceHandlers(registry);
    }
}
