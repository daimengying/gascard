package com.lirong.gascard.interceptor;

import com.alibaba.druid.util.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;

/**
 * @author daimengying
 * 登录拦截器
 */
@Component
public class LoginInterceptor extends HandlerInterceptorAdapter {
    private Logger _logger = LogManager.getLogger();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String url = request.getServletPath();
        String contextPath=request.getContextPath();
        _logger.warn("进入拦截器 ..." + "请求地址：" + url);
        Object user = request.getSession().getAttribute("userInfo");
        if(user==null){
            String query = request.getQueryString();
            if(!StringUtils.isEmpty(query)){
                url = url + "?" + query;
            }
            response.sendRedirect(contextPath+"/toLogin?redirectURL="
                    + URLEncoder.encode(url,"UTF-8"));
            return false;
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
        super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        super.afterCompletion(request, response, handler, ex);
    }
}
