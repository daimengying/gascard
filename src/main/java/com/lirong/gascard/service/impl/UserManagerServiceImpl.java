package com.lirong.gascard.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageRowBounds;
import com.lirong.gascard.aspect.CacheRemove;
import com.lirong.gascard.dao.AdminLogMapper;
import com.lirong.gascard.dao.UserMapper;
import com.lirong.gascard.dao.UserMenuMapper;
import com.lirong.gascard.domain.AdminLog;
import com.lirong.gascard.domain.UserMenu;
import com.lirong.gascard.domain.Users;
import com.lirong.gascard.service.UserManagerService;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @Author: daimengying
 * @Date: 2018/5/21 18:55
 * @Description:
 */
@Service
public class UserManagerServiceImpl implements UserManagerService {
    private Logger _logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private UserMapper userMapper;

    @Resource
    UserMenuMapper userMenuMapper;

    @Resource
    AdminLogMapper adminLogMapper;

    /**
     * 管理员列表
     * @param jsonParam
     * @return
     */
    @Override
    @Cacheable(value = "lirong_gascard_", key = "'getUserListByExampleAndPage_pageNo'+ " +
            "#jsonParam.getString('pageNo')+ '_pageSize'+#jsonParam.getString('pageSize')+'_roleType'+ #jsonParam.getString('roleType')+ '_username'+" +
            "#jsonParam.getString('username')+ '_name'+#jsonParam.getString('name')")
    public List<Users> getUserListByExampleAndPage(JSONObject jsonParam) {
        Example example=new Example(Users.class);
        Example.Criteria createCriteria = example.createCriteria();
        createCriteria.andEqualTo("status",0);
        String roleType=jsonParam.getString("roleType");
        String username=jsonParam.getString("username");
        String name=jsonParam.getString("name");
        String sortName=jsonParam.getString("sortName");
        String sortOrder=jsonParam.getString("sortOrder");
        try{
            Integer pageNo=Integer.parseInt(StrUtil.isBlank(jsonParam.getString("pageNo"))?"1":jsonParam.getString("pageNo"));//默认第一页
            Integer pageSize=Integer.parseInt(StrUtil.isBlank(jsonParam.getString("pageSize"))?"10":jsonParam.getString("pageSize"));//默认一页十行
            if (StrUtil.isNotBlank(roleType)){
                createCriteria.andEqualTo("roleType",Integer.parseInt(roleType));
            }
            if(StrUtil.isNotBlank(username)){
                createCriteria.andLike("username", "%"+username+"%");
            }
            if (StrUtil.isNotBlank(name)){
                createCriteria.andLike("name", "%"+name+"%");
            }
            if(StrUtil.isNotBlank(sortName)&&StrUtil.isNotBlank(sortOrder)){
                example.setOrderByClause(sortName+"  "+sortOrder);
            }else{
                example.setOrderByClause("create_time  desc");
            }
            List<Users> getUserListByExampleAndPage=userMapper.selectByExampleAndRowBounds(example,new PageRowBounds((pageNo-1)*pageSize, pageSize));
            return getUserListByExampleAndPage;
        }catch (Exception e){
            _logger.error("【权限管理模块】管理员列表异常，错误信息：" + e.getMessage());
            return null;
        }


    }

    /**
     * 根据条件查询users表总数
     * @param param
     * @return
     */
    @Override
    @Cacheable(value = "lirong_gascard_",key="'usercount_roleType'+#param.get('roleType')+'_username'+#param.get('username')+'_name'+#param.get('name')")
    public Integer getCountByExample(Map<String, Object> param) {
        try{
            Example example=new Example(Users.class);
            Example.Criteria createCriteria = example.createCriteria();
            createCriteria.andEqualTo("status",0);
            String roleType=param.get("roleType")+  "";
            String username=param.get("username")+"";
            String name=param.get("name")+"";
            if (StrUtil.isNotBlank(roleType)){
                createCriteria.andEqualTo("roleType",Integer.parseInt(roleType));
            }
            if(StrUtil.isNotBlank(username)){
                createCriteria.andEqualTo("username",username);
            }
            if (StrUtil.isNotBlank(name)){
                createCriteria.andEqualTo("name",name);
            }
            return userMapper.selectCountByExample(example);
        }catch (Exception e){
            _logger.error("【权限管理模块】获取用户数异常，错误信息：" + e.getMessage());
            return 0;
        }
    }

    /**
     * 根据id获取user
     * @param userid
     * @return
     */
    @Override
    //@Cacheable(value = "86400", key = "'***_city_list'")这里的value就是失效时长
    @Cacheable(value = "lirong_gascard_",key="'user_'+#userid")
    public Users getUserById(@NonNull Integer userid) {
        System.out.println("---------"+userid+"缓存失效，从DB取数据----------");
        return userMapper.selectByPrimaryKey(userid);
    }

    /**
     * 更新用户信息
     * @param user
     * @return
     */
    @Override
    @CachePut(value = "lirong_gascard_",key="'user_*'") //CachePut和Cacheable的key相同，返回类型也要相同
    @CacheRemove(value = "lirong_gascard_",key="'getUserListByExampleAndPage*'")
    public Users updateUserByPK(Users user) {
        Integer num= userMapper.updateByPrimaryKeySelective(user);
        if(num<1){
            throw new   RuntimeException("更新用户信息失败");
        }else{
            return user;
        }

    }

    /**
     * 添加用户
     * @param user
     * @return
     */
    @Override
    @CacheEvict(value="lirong_gascard_",key="'usercount*'",beforeInvocation=true)
    @CacheRemove(value = "lirong_gascard_",key="'getUserListByExampleAndPage*'")
    public Integer addUser(Users user) {
        return userMapper.insertSelective(user);
    }

    /**
     * 根据userid删除用户
     * @param userid
     * @return
     */
    @Override
    @CacheEvict(value="lirong_gascard_",key="'usercount*'",beforeInvocation=true)
    @CacheRemove(value = "lirong_gascard_",key="'getUserListByExampleAndPage*'")
    public Integer deleteUser(@NonNull Integer userid) {

        return userMapper.deleteByPrimaryKey(userid);
    }

    /**
     * 根据userid menuId定位到UserMenu
     * @param userid
     * @param menuId
     * @return
     */
    @Override
    public UserMenu getUserMenuByExample(Integer userid,Integer menuId) {
        try{
            Example example=new Example(UserMenu.class);
            Example.Criteria createCriteria = example.createCriteria();
            createCriteria.andEqualTo("userId",userid);
            createCriteria.andEqualTo("menuId",menuId);
            return userMenuMapper.selectOneByExample(example);
        }catch (Exception e){
            _logger.error("【权限管理模块】获取用户菜单关联异常，错误信息：" + e.getMessage());
            return null;
        }

    }

    /**
     * 根据主键Id 更新用户菜单表
     * @param userMenu
     * @return
     */
    @Override
    public UserMenu updateUserMenuByPk(UserMenu userMenu) {
        Integer num= userMenuMapper.updateByPrimaryKeySelective(userMenu);
        if(num<1){
            throw new   RuntimeException("更新用户菜单表失败");
        }else{
            return userMenu;
        }
    }

    /**
     * 根据用户删除他所有的用户菜单关系
     * @param userId
     * @return
     */
    @Override
    @CacheEvict(value="lirong_gascard_",key="'userMenuListByUserId_'+#userId",beforeInvocation=true)
    public void deleteUserMenu(Integer userId) {
        /*try{
            Example example=new Example(UserMenu.class);
            Example.Criteria createCriteria = example.createCriteria();
            createCriteria.andEqualTo("userId",userId);
            return userMenuMapper.deleteByExample(example);
        }catch (Exception e){
            _logger.error("【权限管理模块】删除用户菜单关联异常，错误信息：" + e.getMessage());
            return null;
        }*/
        Example example=new Example(UserMenu.class);
        Example.Criteria createCriteria = example.createCriteria();
        createCriteria.andEqualTo("userId",userId);
        userMenuMapper.deleteByExample(example);

    }

    /**
     * 插入一条新的用户菜单关系记录
     * @param userMenu
     * @return
     */
    @Override
    @CachePut(value = "lirong_gascard_",key="'userMenuListByUserId_'+#userId")
    public Integer addUserMenu(UserMenu userMenu) {
        return userMenuMapper.insertSelective(userMenu);
    }

    /**
     * 插入一条管理员操作日志
     * @param adminLog
     * @return
     */
    @Override
    public Integer addAdminLog(AdminLog adminLog) {
        return adminLogMapper.insertSelective(adminLog);
    }

    /**
     * 更新代理商余额异步事件
     * @param updateUser
     * @return
     */
    @Async
    @Override
    public Integer updateAgentBalance(Users updateUser) {
        return userMapper.updateByPrimaryKeySelective(updateUser);
    }
}
