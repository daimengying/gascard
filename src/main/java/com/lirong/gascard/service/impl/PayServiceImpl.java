package com.lirong.gascard.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageRowBounds;
import com.lirong.gascard.dao.AgentChargeLogMapper;
import com.lirong.gascard.dao.PayLogMapper;
import com.lirong.gascard.domain.AgentChargeLog;
import com.lirong.gascard.domain.PayLog;
import com.lirong.gascard.service.PayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @Author: daimengying
 * @Date: 2018/5/26 16:56
 * @Description:
 */
@Service
public class PayServiceImpl implements PayService {
    private Logger _logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private PayLogMapper payLogMapper;

    @Resource
    private AgentChargeLogMapper agentChargeLogMapper;

    /**
     * 新增消费记录
     * @param payLog
     * @return
     */
    @Async
    @Override
    public Integer addPay(PayLog payLog) {
        return payLogMapper.insertSelective(payLog);
    }

    /**
     * 新增一条代理商充值记录
     * @param agentChargeLog
     * @return
     */
    @Override
    public Integer addAgentChargeLog(AgentChargeLog agentChargeLog) {
        return agentChargeLogMapper.insertSelective(agentChargeLog);
    }

    /**
     * 条件查询，代理商充值记录的数量
     * @param jsonParam
     * @return
     */
    @Override
    public Integer getAgentChargeLogCount(JSONObject jsonParam) {
        String account=jsonParam.getString("account");
        String beginTime=jsonParam.getString("beginTime");
        String endTime=jsonParam.getString("endTime");
        String payType=jsonParam.getString("payType");

        try{
            Example example=new Example(AgentChargeLog.class);
            Example.Criteria createCriteria = example.createCriteria();
            createCriteria.andEqualTo("status",0);
            if(StrUtil.isNotBlank(account) && !"-1".equals(account) ){
                createCriteria.andLike("account", "%"+account+"%");
            }
            if(StrUtil.isNotBlank(payType) && Integer.parseInt(payType) != -1){
                createCriteria.andEqualTo("payType",payType);
            }
            if(StrUtil.isNotBlank(beginTime)&&StrUtil.isNotBlank(endTime)){
                createCriteria.andBetween("optionTime", beginTime, endTime);
            }else if(StrUtil.isNotBlank(beginTime)){
                createCriteria.andBetween("optionTime", beginTime, new Date());
            }
            return agentChargeLogMapper.selectCountByExample(example);
        }catch (Exception e){
            _logger.error("【财务管理模块】获取代理商充值记录数异常，错误信息：" + e.getMessage());
            return 0;
        }
    }

    /**
     * 分页查询代理商充值记录，作为财务管理表格数据
     * @param jsonParam
     * @return
     */
    @Override
    public List<AgentChargeLog> getAgentChargeLogListByExampleAndPage(JSONObject jsonParam) {
        String account=jsonParam.getString("account");
        String beginTime=jsonParam.getString("beginTime");
        String endTime=jsonParam.getString("endTime");
        String payType=jsonParam.getString("payType");

        try{
            Integer pageNo=Integer.parseInt(StrUtil.isBlank(jsonParam.getString("pageNo"))?"1":jsonParam.getString("pageNo"));//默认第一页
            Integer pageSize=Integer.parseInt(StrUtil.isBlank(jsonParam.getString("pageSize"))?"10":jsonParam.getString("pageSize"));//默认一页十行

            Example example=new Example(AgentChargeLog.class);
            Example.Criteria createCriteria = example.createCriteria();
            createCriteria.andEqualTo("status",0);

            if(StrUtil.isNotBlank(account) && !"-1".equals(account) ){
                createCriteria.andLike("account","%"+account+"%");
            }
            if(StrUtil.isNotBlank(payType) && Integer.parseInt(payType) != -1){
                createCriteria.andEqualTo("payType",payType);
            }
            if(StrUtil.isNotBlank(beginTime)&&StrUtil.isNotBlank(endTime)){
                createCriteria.andBetween("optionTime", beginTime, endTime);
            }else if(StrUtil.isNotBlank(beginTime)){
                createCriteria.andBetween("optionTime", beginTime, new Date());
            }

            example.setOrderByClause("option_time desc");

            List<AgentChargeLog> listByEaxmpleAndPage = agentChargeLogMapper.selectByExampleAndRowBounds(example,new PageRowBounds((pageNo-1)*pageSize, pageSize));
            return listByEaxmpleAndPage;
        }catch (Exception e){
            _logger.error("【财务管理模块】财务管理列表异常，错误信息：" + e.getMessage());
            return null;
        }
    }

    /**
     * 分页查询充值日志，作为消费明细表格数据
     * @param jsonParam
     * @return
     */
    @Override
    public List<PayLog> getPayLogListByExampleAndPage(JSONObject jsonParam) {
        String account=jsonParam.getString("account");
        String beginTime=jsonParam.getString("beginTime");
        String endTime=jsonParam.getString("endTime");
        String type=jsonParam.getString("type");
        Integer roleType=jsonParam.getInteger("roleType");
        String loginAccount=jsonParam.getString("loginAccount");
        try{
            Integer pageNo=Integer.parseInt(StrUtil.isBlank(jsonParam.getString("pageNo"))?"1":jsonParam.getString("pageNo"));//默认第一页
            Integer pageSize=Integer.parseInt(StrUtil.isBlank(jsonParam.getString("pageSize"))?"10":jsonParam.getString("pageSize"));//默认一页十行

            Example example=new Example(PayLog.class);
            Example.Criteria createCriteria = example.createCriteria();
            createCriteria.andEqualTo("status",0);

            //区分管理员和普通用户，普通代理商只能看到他们自己的消费明细
            if(roleType==3){
                createCriteria.andEqualTo("account",loginAccount);
            }else if(roleType==1||roleType==2){
                if(StrUtil.isNotBlank(account) && !"-1".equals(account) ){
                    createCriteria.andLike("account","%"+account+"%");
                }
            }
            if(StrUtil.isNotBlank(type) && Integer.parseInt(type) != -1){
                createCriteria.andEqualTo("type",type);
            }
            if(StrUtil.isNotBlank(beginTime)&&StrUtil.isNotBlank(endTime)){
                createCriteria.andBetween("optionTime", beginTime, endTime);
            }else if(StrUtil.isNotBlank(beginTime)){
                createCriteria.andBetween("optionTime", beginTime, new Date());
            }
            example.setOrderByClause("option_time desc");
            List<PayLog> listByEaxmpleAndPage =payLogMapper.selectByExampleAndRowBounds(example,new PageRowBounds((pageNo-1)*pageSize, pageSize));

            return listByEaxmpleAndPage;
        }catch (Exception e){
            _logger.error("【订单管理模块】消费明细列表异常，错误信息：" + e.getMessage());
            return null;
        }
    }

    /**
     * 根据条件获取充值日志中的数据量
     * @param jsonParam
     * @return
     */
    @Override
    public Integer getPayLogCount(JSONObject jsonParam) {
        String account=jsonParam.getString("account");
        String beginTime=jsonParam.getString("beginTime");
        String endTime=jsonParam.getString("endTime");
        String type=jsonParam.getString("type");
        Integer roleType=jsonParam.getInteger("roleType");
        String loginAccount=jsonParam.getString("loginAccount");
        try {
            Example example=new Example(PayLog.class);
            Example.Criteria createCriteria = example.createCriteria();
            createCriteria.andEqualTo("status",0);

            //区分管理员和普通用户，普通代理商只能看到他们自己的消费明细
            if(roleType==1||roleType==2){
                if(StrUtil.isNotBlank(account) && !"-1".equals(account) ){
                    createCriteria.andLike("account","%"+account+"%");
                }
            }else if(roleType==3){
                createCriteria.andEqualTo("account",loginAccount);
            }
            if(StrUtil.isNotBlank(type) && Integer.parseInt(type) != -1){
                createCriteria.andEqualTo("type",type);
            }
            if(StrUtil.isNotBlank(beginTime)&&StrUtil.isNotBlank(endTime)){
                createCriteria.andBetween("optionTime", beginTime, endTime);
            }else if(StrUtil.isNotBlank(beginTime)){
                createCriteria.andBetween("optionTime", beginTime, new Date());
            }
            return payLogMapper.selectCountByExample(example);
        }catch (Exception e){
            _logger.error("【订单管理模块】查询消费明细数量异常，错误信息：" + e.getMessage());
            return null;
        }
    }
}
