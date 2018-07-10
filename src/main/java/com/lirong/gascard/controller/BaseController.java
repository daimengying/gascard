package com.lirong.gascard.controller;

import com.lirong.gascard.domain.Menu;
import com.lirong.gascard.domain.Users;
import com.lirong.gascard.service.MenuService;
import com.lirong.gascard.vo.Tree;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @Author: daimengying
 * @Date: 2018/5/23 09:39
 * @Description:
 */
public class BaseController {
    @Autowired
    MenuService menuService;

    /**
     * 获取当前登录用户
     * @param req
     * @return
     */
    public Users getCurrentUser(HttpServletRequest req){
        return (Users)getSession(req).getAttribute("userInfo");
    }

    public HttpSession getSession(HttpServletRequest req) {
        return req.getSession();
    }

    /**
     * 获取用户菜单树存放到session
     * @param req
     */
    public void menuTreeToSession(HttpServletRequest req){
        HttpSession session = getSession(req);
        if(StringUtils.isEmpty(session.getAttribute("menuTree"))){
            Users user=getCurrentUser(req);
            List<Tree<Menu>> trees=menuService.getMenuTree(user.getId());
            session.setAttribute("menuTree",trees);
        }

    }

}
