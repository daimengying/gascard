package com.lirong.gascard.service;

import com.alibaba.fastjson.JSONObject;
import com.lirong.gascard.domain.AdminLog;
import com.lirong.gascard.domain.UserMenu;
import com.lirong.gascard.domain.Users;
import lombok.NonNull;
import org.apache.catalina.User;

import java.util.List;
import java.util.Map;

/**
 * @Author: daimengying
 * @Date: 2018/5/21 18:54
 * @Description:
 */
public interface UserManagerService {
    List<Users> getUserListByExampleAndPage(JSONObject jsonParam);

    Integer getCountByExample(Map<String,Object>param);

    Users getUserById(@NonNull Integer userid);

    Users updateUserByPK(Users user);

    Integer addUser(Users user);

    Integer deleteUser(@NonNull Integer userid);

    UserMenu getUserMenuByExample(Integer userid,Integer menuId);

    UserMenu updateUserMenuByPk(UserMenu userMenu);

    void deleteUserMenu(@NonNull  Integer userId);

    Integer addUserMenu(UserMenu userMenu);

    Integer addAdminLog(AdminLog adminLog);

    Integer updateAgentBalance(Users updateUser);

}