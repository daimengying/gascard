package com.lirong.gascard.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lirong.gascard.domain.ChargeOrder;
import com.lirong.gascard.domain.Users;
import com.lirong.gascard.service.ChargeOrderService;
import com.lirong.gascard.service.LoginService;
import com.lirong.gascard.utils.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author: daimengying
 * @Date: 2018/6/6 08:52
 * @Description:接收上游回调通知
 */
@EnableAsync
@Controller
@RequestMapping("/notify")
public class NotifyController {
    private Logger _logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ChargeOrderService chargeOrderService;

    @Autowired
    LoginService loginService;

    @RequestMapping(value="/kachi",method= RequestMethod.POST)
    @ResponseBody
    public String kachi(HttpServletRequest request){
        try{
            String jsonString = HttpUtil.getJsonString(request);
            if(jsonString  == null){
                return null;
            }
            _logger.info("{} 充值结果回调通知： {}   {}",request.getRemoteAddr(),request.getRequestURI(),jsonString);
            JSONObject jsonObject = JSON.parseObject(jsonString);
            String resultCode = jsonObject.getString("resultCode");
            String upOrderId = jsonObject.getString("bizid");
            ChargeOrder chargeOrder=chargeOrderService.findOrderByUpOrderId(upOrderId);
            if(chargeOrder!=null){
                //异步更新订单表
                chargeOrderService.updateChargeStatus(jsonObject,chargeOrder);
                if ("T00003".equals(resultCode)) {
                    //充值成功 异步更新消费明细和代理商余额
                    Users agent=loginService.getUserByUserName(chargeOrder.getAccount()).get(0);
                    chargeOrderService.addPayLogAndUpdateAgent(agent,chargeOrder);
                    chargeOrder.setChargeStatus(4);
                }else if("T00004".equals(resultCode)) {
                    //充值失败
                    chargeOrder.setChargeStatus(5);
                }
                if(!StringUtils.isEmpty(chargeOrder.getBackUrl())){
                    //给下游推送充值结果
                    chargeOrderService.callback(chargeOrder);
                }
            }

        }catch (Exception e){
            _logger.error("【充值模块】接收上游回调通知异常，错误信息：" + e.getMessage());
        }
        return "1";
    }



}
