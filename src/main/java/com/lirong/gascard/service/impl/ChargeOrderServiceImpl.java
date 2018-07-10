package com.lirong.gascard.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageRowBounds;
import com.lirong.gascard.dao.*;
import com.lirong.gascard.domain.*;
import com.lirong.gascard.service.ChargeOrderService;
import com.lirong.gascard.service.SubmitOrderToChannelService;
import com.lirong.gascard.utils.RestUtils;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.*;


/**
 * @Author: daimengying
 * @Date: 2018/6/1 15:48
 * @Description:订单
*/


@Service
public class ChargeOrderServiceImpl implements ChargeOrderService {
    private Logger _logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    ChargeOrderMapper chargeOrderMapper;


    @Resource
    PayLogMapper payLogMapper;

    @Resource
    UserMapper userMapper;

    @Resource
    CallBackMapper callBackMapper;

    @Resource
    private RestUtils restUtil;

    @Resource
    SubmitOrderToChannelService submitOrderToChannelService;

    /**
     * 校验每个账户提交的订单号是否重复
     * @param account
     * @param orderId
     * @return
     * */
    @Override
    public int verifyOrderId(String account, String orderId) {
        Example example = new Example(ChargeOrder.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("account",account);
        criteria.andEqualTo("orderId",orderId);
        return  chargeOrderMapper.selectCountByExample(example);
    }

    /**
     * 添加一条充值记录
     * @param chargeOrder
     * @return
     */
    @Async
    @Override
    public Integer addOrder(ChargeOrder chargeOrder) {
        return chargeOrderMapper.insertSelective(chargeOrder);
    }


    /**
     * 提交订单到上游充值
     * @param chargeOrder
     */
    @Async
    @Override
    public void submitOrderToUp(ChargeOrder chargeOrder) {
        String channel= chargeOrder.getChannel();
        switch(channel){
            case "KaChi":
                chargeOrder=submitOrderToChannelService.submitToCachi(chargeOrder);
                break;
        }

    }

    /**
     * 我方推送充值结果到下游
     * 回调间隔5分钟  推送3次或者收到"ok"确认后停止推送
     * @param chargeOrder
     */
    @Async
    @Override
    public void callback(ChargeOrder chargeOrder) {
        CallBack callback = new CallBack();
        callback.setAccount(chargeOrder.getAccount());
        callback.setCardnum(chargeOrder.getCardnum());
        callback.setUrl(chargeOrder.getBackUrl());
        callback.setOrderId(chargeOrder.getId());

        String backrurl=chargeOrder.getBackUrl();
        Map<String, Object> params = new HashMap<>();
        params.put("taskId", chargeOrder.getUpOrderId());
        params.put("orderId", chargeOrder.getOrderId());
        params.put("cardnum", chargeOrder.getCardnum());
        params.put("status", chargeOrder.getChargeStatus());
        params.put("reportTime",chargeOrder.getReportTime());

        callback.setRequest(JSONObject.toJSONString(params));
        for (int i = 0; i < 3; i++) {
            try {
                String restCallResult=restUtil.restCallExchange(backrurl,params);
                if(StringUtils.isEmpty(restCallResult)){
                    throw new Exception("返回内容为空");
                }else{
                    callback.setResponse(restCallResult);
                    if ("ok".equals(restCallResult)) {
                        break;// 收到ok确认后 不再推送
                    }else{
                        try {
                            Thread.sleep(30000);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                        continue;
                    }
                }

            }catch (Exception e) {
                // 出现异常就重试
                _logger.error("【充值模块】推送充值结果给下游 {} 异常，错误信息：",chargeOrder.getBackUrl(),e.getMessage());
                try {
                    Thread.sleep(30000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                continue;
            }
        }
        //插入一条充值结果通知下游的记录
        callBackMapper.insertSelective(callback);
    }

    /**
     * 根据主键ID更新订单记录
     * @param chargeOrder
     * @return
     */
    @Override
    public Integer updateChargeOrderByPk(ChargeOrder chargeOrder) {
        return chargeOrderMapper.updateByPrimaryKeySelective(chargeOrder);
    }

    /**
     * 根据订单orderId和代理商账户获取单条充值记录
     * @param account
     * @param orderId
     * @return
     */
    @Override
    public ChargeOrder getOneChargeOrder(String account, String orderId) {
        Example example = new Example(ChargeOrder.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("account",account);
        criteria.andEqualTo("orderId",orderId);
        return chargeOrderMapper.selectOneByExample(example);
    }



    /**
     * 根据提交到上游的订单Id查询订单记录
     * @param upOrderId
     * @return
     */
    @Override
    public ChargeOrder findOrderByUpOrderId(@NonNull String upOrderId) {
        Example example=new Example(ChargeOrder.class);
        Example.Criteria createCriteria = example.createCriteria();
        createCriteria.andEqualTo("upOrderId", upOrderId);
        createCriteria.andIn("chargeStatus", Arrays.asList(1,2));
        return chargeOrderMapper.selectOneByExample(example);
    }

    /**
     * 上游回调后，更新订单状态
     * @param jsonObject
     * @return
     */
    @Async
    @Override
    public void updateChargeStatus(JSONObject jsonObject,ChargeOrder chargeOrder) {
        String resultCode = jsonObject.getString("resultCode");
        String resultMsg = jsonObject.getString("resultMsg");
        if(chargeOrder!=null){
            Integer status=chargeOrder.getChargeStatus();
            if("T00003".equals(resultCode)){
                status=4;
            }else if("T00004".equals(resultCode)) {
                status=5;
            }
            chargeOrder.setChargeStatus(status);
            chargeOrder.setReportTime(new Date());
            chargeOrder.setMemo(resultMsg);
            this.updateChargeOrderByPk(chargeOrder);
        }
    }


    /**
     * 插入一条消费明细记录,且修改users表代理商余额
     * @param user
     * @param chargeOrder
     */
    @Async
    @Override
    public void addPayLogAndUpdateAgent(Users user, ChargeOrder chargeOrder) {
        Double paylogMoney=0.0D-chargeOrder.getChargePrice();
        //插入一条消费明细记录
        PayLog payLog=new PayLog();
        payLog.setUserId(user.getId());
        payLog.setAccount(user.getUsername());
        payLog.setAgent(user.getAgent());
        payLog.setMoney(paylogMoney);
        payLog.setType(2);
        payLog.setBalance(user.getBalance()+paylogMoney);
        payLog.setMemo("订单编号"+chargeOrder.getId() + "卡号"+chargeOrder.getCardnum()+"面值"+chargeOrder.getAmount());
        Integer num=payLogMapper.insertSelective(payLog);
        if(num>0){
            //修改users表代理商余额
            userMapper.updateAgentBalance(user.getUsername(),paylogMoney);
        }

    }

    /**
     * 分页查询订单表，作为充值记录表格数据
     * @param jsonParam
     * @return
     */
    @Override
    public List<ChargeOrder> getOrderListByExampleAndPage(JSONObject jsonParam) {
        String account=jsonParam.getString("account");
        String beginTime=jsonParam.getString("beginTime");
        String endTime=jsonParam.getString("endTime");
        String type=jsonParam.getString("type");
        Integer roleType=jsonParam.getInteger("roleType");
        String loginAccount=jsonParam.getString("loginAccount");
        String cardnum=jsonParam.getString("cardnum");
        String amount=jsonParam.getString("amount");
        String chargeStatus=jsonParam.getString("chargeStatus");
        try{
            Integer pageNo=Integer.parseInt(StrUtil.isBlank(jsonParam.getString("pageNo"))?"1":jsonParam.getString("pageNo"));//默认第一页
            Integer pageSize=Integer.parseInt(StrUtil.isBlank(jsonParam.getString("pageSize"))?"10":jsonParam.getString("pageSize"));//默认一页十行

            Example example=new Example(ChargeOrder.class);
            Example.Criteria createCriteria = example.createCriteria();
            //区分管理员和普通用户，普通代理商只能看到他们自己的消费明细
            if(roleType==3){
                createCriteria.andEqualTo("account",loginAccount);
            }else {
                if(StrUtil.isNotBlank(account) && !"-1".equals(account) ){
                    createCriteria.andLike("account","%"+account+"%");
                }
            }
            if(StrUtil.isNotBlank(type) && Integer.parseInt(type) != -1){
                createCriteria.andEqualTo("type",type);
            }
            if(StrUtil.isNotBlank(cardnum) ){
                createCriteria.andEqualTo("cardnum",cardnum);
            }
            if(StrUtil.isNotBlank(amount) ){
                createCriteria.andEqualTo("amount",amount);
            }
            if(StrUtil.isNotBlank(chargeStatus) && Integer.parseInt(chargeStatus) != -1){
                createCriteria.andEqualTo("chargeStatus",chargeStatus);
            }
            if(StrUtil.isNotBlank(beginTime)&&StrUtil.isNotBlank(endTime)){
                createCriteria.andBetween("optionTime", beginTime, endTime);
            }else if(StrUtil.isNotBlank(beginTime)){
                createCriteria.andBetween("optionTime", beginTime, new Date());
            }
            return chargeOrderMapper.selectByExampleAndRowBounds(example,new PageRowBounds((pageNo-1)*pageSize, pageSize));
        }catch (Exception e){
            _logger.error("【订单管理模块】充值记录列表异常，错误信息：" + e.getMessage());
            return null;
        }
    }

    @Override
    public Integer getChargeOrderCount(JSONObject jsonParam) {
        String account=jsonParam.getString("account");
        String beginTime=jsonParam.getString("beginTime");
        String endTime=jsonParam.getString("endTime");
        String type=jsonParam.getString("type");
        Integer roleType=jsonParam.getInteger("roleType");
        String loginAccount=jsonParam.getString("loginAccount");
        String cardnum=jsonParam.getString("cardnum");
        String amount=jsonParam.getString("amount");
        String chargeStatus=jsonParam.getString("chargeStatus");
        try{
            Example example=new Example(ChargeOrder.class);
            Example.Criteria createCriteria = example.createCriteria();
            //区分管理员和普通用户，普通代理商只能看到他们自己的消费明细
            if(roleType==3){
                createCriteria.andEqualTo("account",loginAccount);
            }else {
                if(StrUtil.isNotBlank(account) && !"-1".equals(account) ){
                    createCriteria.andLike("account","%"+account+"%");
                }
            }
            if(StrUtil.isNotBlank(type) && Integer.parseInt(type) != -1){
                createCriteria.andEqualTo("type",type);
            }
            if(StrUtil.isNotBlank(cardnum) ){
                createCriteria.andEqualTo("cardnum",cardnum);
            }
            if(StrUtil.isNotBlank(amount) ){
                createCriteria.andEqualTo("amount",amount);
            }
            if(StrUtil.isNotBlank(chargeStatus) && Integer.parseInt(chargeStatus) != -1){
                createCriteria.andEqualTo("chargeStatus",chargeStatus);
            }
            if(StrUtil.isNotBlank(beginTime)&&StrUtil.isNotBlank(endTime)){
                createCriteria.andBetween("optionTime", beginTime, endTime);
            }else if(StrUtil.isNotBlank(beginTime)){
                createCriteria.andBetween("optionTime", beginTime, new Date());
            }
            return chargeOrderMapper.selectCountByExample(example);
        }catch (Exception e){
            _logger.error("【订单管理模块】获取充值记录数量异常，错误信息：" + e.getMessage());
            return null;
        }
    }
}
