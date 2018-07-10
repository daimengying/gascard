package com.lirong.gascard.service.impl;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.lirong.gascard.dao.IpblackMapper;
import com.lirong.gascard.dao.LoginFailLogMapper;
import com.lirong.gascard.dao.UserMapper;
import com.lirong.gascard.domain.Ipblack;
import com.lirong.gascard.domain.LoginFailLog;
import com.lirong.gascard.domain.Users;
import com.lirong.gascard.service.LoginService;
import com.lirong.gascard.utils.UserAgentUtil;
import com.lirong.gascard.utils.ip.IpUtil;
import com.lirong.gascard.vo.UserAgent;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: daimengying
 * @Date: 2018/5/17 17:42
 * @Description:登录逻辑处理
 */
@Service
public class LoginServiceImpl implements LoginService {
    private Logger _logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private UserMapper userMapper;

    @Resource
    private LoginFailLogMapper failLogMapper;

    @Resource
    private IpblackMapper ipblackMapper;

    @Resource(name="ipUtil")
    private IpUtil ipUtil;


    @Override
    @Cacheable(value = "lirong_gascard_",key="'user_'+#userName")
    public List<Users> getUserByUserName(@NonNull String userName) {
        Example example=new Example(Users.class);
        Example.Criteria createCriteria = example.createCriteria();
        createCriteria.andEqualTo("username",userName);
        createCriteria.andEqualTo("status",0);
        List<Users> userList=userMapper.selectByExample(example);
        return userList;
    }

    @Override
    public Map<String, String> loginLogic(Map<String, Object>param, HttpServletRequest request, HttpServletResponse response) {
        Map<String, String>result=new HashMap<>();
        String userName = param.get("username")+"";
        String passWord = param.get("password")+"";
        Boolean loginFlag=false;
        UserAgent userAgent = UserAgentUtil.getUserAgent(request.getHeader("User-Agent"));
        userAgent.setIp(ipUtil.getIpAddr(request));

        try{
            List<Users> userList=getUserByUserName(userName);
            if(userList==null){
                result.put("resultCode", "-2001");
                result.put("resultMsg", "用户不存在");
                return result;
            }
            for(Users user:userList){
                if(user.getPassword().equals(passWord)){
                    loginFlag=true;
                    //将登录用户信息放入session
                    user.setUserAgent(userAgent);
                    HttpSession session = request.getSession();
                    session.setAttribute("userInfo", user);
                    result.put("resultCode", "0");
                    break;
                }
            }
            if(!loginFlag){
                //密码错误
                result.put("resultCode", "-2002");
                result.put("resultMsg", "用户名或密码错误");
            }
            if(!"0".equals(result.get("resultCode"))){
                //登陆失败记录日志
                LoginFailLog failLog=new LoginFailLog();
                failLog.setUsername(userName);
                failLog.setPassword(passWord);
                failLog.setLoginTime(new Date());
                BeanUtils.copyProperties(userAgent,failLog);
                failLogMapper.insert(failLog);
                //登录失败做IP限定，5次
                Example example = new Example(LoginFailLog.class);
                Example.Criteria criteria = example.createCriteria();
                criteria.andEqualTo("ip",ipUtil.getIpAddr(request));
                String today = DateUtil.format(new Date(), "yyyy-MM-dd");
                criteria.andBetween("loginTime",today+" 00:00:00",today+" 23:59:59");
                Integer count = failLogMapper.selectCountByExample(example);
                if(count >= 5){
                    Ipblack ipblack = new Ipblack();
                    ipblack.setIp(ipUtil.getIpAddr(request));
                    ipblack.setOptionTime(new Date());
                    ipblackMapper.insert(ipblack);
                    result.put("resultCode", "-2003");
                    result.put("resultMsg","IP限制");
                }else{
                    result.put("resultCode", "-2004");
                    result.put("resultMsg",result.get("resultMsg")+",您还可以尝试"+(5-count)+"次");
                }
                return result;
            }

        }catch (Exception e){
            _logger.error("【系统登录模块】登录异常，错误信息：" + e.getMessage());
            result.put("resultCode", "-2005");
            result.put("resultMsg", "系统异常");
        }
        return result;
    }

    /**
     * 更新密码
     * @param user
     * @param repeatPassword
     * @return
     */
    @Override
    @CachePut(value = "lirong_gascard_",key="'user'+#user.id")
    public Users updateUserByPK(Users user, String repeatPassword) {
        user.setPassword(repeatPassword);
        Integer num= userMapper.updateByPrimaryKeySelective(user);
        if(num<1){
            throw new RuntimeException("更新用户密码失败");
        }else{
            return user;
        }
    }

}
