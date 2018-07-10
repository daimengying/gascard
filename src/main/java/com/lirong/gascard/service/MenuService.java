package com.lirong.gascard.service;

import com.lirong.gascard.domain.Menu;
import com.lirong.gascard.domain.UserMenu;
import com.lirong.gascard.vo.Tree;
import lombok.NonNull;

import java.util.List;


/**
 * @Author: daimengying
 * @Date: 2018/5/19 14:33
 * @Description:
 */
public interface MenuService {
    List<Tree<Menu>> getMenuTree(@NonNull Integer userId);

    List<Menu>getMenuList();

    List<UserMenu>getMenuListByUserId(@NonNull Integer userId);

}
