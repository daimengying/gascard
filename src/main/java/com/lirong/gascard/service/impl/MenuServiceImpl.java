package com.lirong.gascard.service.impl;

import com.lirong.gascard.dao.MenuMapper;
import com.lirong.gascard.dao.UserMenuMapper;
import com.lirong.gascard.domain.Menu;
import com.lirong.gascard.domain.UserMenu;
import com.lirong.gascard.domain.Users;
import com.lirong.gascard.service.MenuService;
import com.lirong.gascard.utils.BuildTree;
import com.lirong.gascard.vo.Tree;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: daimengying
 * @Date: 2018/5/19 15:37
 * @Description:
 */
@Service
public class MenuServiceImpl implements MenuService {
    @Resource
    private UserMenuMapper userMenuMapper;
    @Resource
    private MenuMapper menuMapper;

    private Logger _logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 根据用户查询菜单树
     * @param userId
     * @return
     */
    @Override
    public List<Tree<Menu>> getMenuTree(@NonNull Integer userId) {
        List<Tree<Menu>> trees = new ArrayList<>();
        try {
            //根据userId获取UserMenu表中该用户所有菜单Id
            List<UserMenu> getUserMenuListByUser=getMenuListByUserId(userId);
            //根据菜单Id查询菜单表菜单信息
            List<Menu> getMenuListByUser=new ArrayList<>();
            for(UserMenu userMenu:getUserMenuListByUser){
                Menu menu=menuMapper.selectByPrimaryKey(userMenu.getMenuId());
                getMenuListByUser.add(menu);
            }
            for (Menu menu : getMenuListByUser) {
                Tree<Menu> tree = new Tree<Menu>();
                tree.setId(menu.getId().toString());
                tree.setParentId(menu.getParentId().toString());
                tree.setText(menu.getName());
                tree.setHref(menu.getHref());
                tree.setIcon(menu.getIcon());
                trees.add(tree);
            }
            return BuildTree.build(trees);
        }catch (Exception e){
            _logger.error("【菜单模块】获取左侧菜单树异常，错误信息：" + e.getMessage());
            return null;
        }

    }

    /**
     * 获取所有菜单列表，用作用户权限管理模块
     * @return
     */
    @Override
    @Cacheable(value = "lirong_gascard_",key="'menuListAll'")
    public List<Menu> getMenuList() {
        try {
            List<Menu> getMenuList=menuMapper.selectAll();
            return getMenuList;
        }catch (Exception e){
            _logger.error("【菜单模块】获取菜单列表异常，错误信息：" + e.getMessage());
            return null;
        }
    }

    /**
     * 根据userId获取
     * @param userId
     * @return
     */
    @Override
    @Cacheable(value = "lirong_gascard_",key="'userMenuListByUserId_'+#userId")
    public List<UserMenu> getMenuListByUserId(Integer userId) {
        try {
            Example example=new Example(UserMenu.class);
            Example.Criteria createCriteria = example.createCriteria();
            createCriteria.andEqualTo("userId",userId);
            //根据userId获取UserMenu表中该用户所有菜单Id
            List<UserMenu> getUserMenuListByUser=userMenuMapper.selectByExample(example);
            return getUserMenuListByUser;
        }catch (Exception e){
            _logger.error("【菜单模块】根据用户Id获取菜单列表异常，错误信息：" + e.getMessage());
            return null;
        }
    }
}
