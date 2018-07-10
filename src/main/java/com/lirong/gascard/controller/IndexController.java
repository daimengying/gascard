package com.lirong.gascard.controller;

import com.lirong.gascard.domain.Menu;
import com.lirong.gascard.domain.Users;
import com.lirong.gascard.service.MenuService;
import com.lirong.gascard.vo.Tree;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author: daimengying
 * @Date: 2018/5/19 14:32
 * @Description:首页（重定向到充值记录）
 */
@Controller
public class IndexController extends BaseController{
    @Autowired
    MenuService menuService;

    @RequestMapping("/")
    public void index(HttpServletRequest request,HttpServletResponse response) throws Exception{
        String contextPath=request.getContextPath();
        response.sendRedirect(contextPath+"/orderManage/toChargeRecord");
    }

}
