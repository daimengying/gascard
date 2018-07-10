package com.lirong.gascard.service;


import com.alibaba.fastjson.JSONObject;
import com.lirong.gascard.domain.ChargeOrder;
import com.lirong.gascard.domain.Users;
import lombok.NonNull;

import java.util.List;

/**
 * @Author: daimengying
 * @Date: 2018/6/1 15:48
 * @Description:充值订单逻辑
 */
public interface ChargeOrderService {
    int verifyOrderId(String account,String orderId);

    Integer addOrder(ChargeOrder chargeOrder);

    void submitOrderToUp(ChargeOrder chargeOrder);

    Integer updateChargeOrderByPk(ChargeOrder chargeOrder);

    ChargeOrder getOneChargeOrder(@NonNull String account,@NonNull String orderId);

    ChargeOrder findOrderByUpOrderId(@NonNull String upOrderId);

    void updateChargeStatus(JSONObject jsonObject,ChargeOrder chargeOrder);

    void addPayLogAndUpdateAgent(Users user, ChargeOrder chargeOrder);

    void callback(ChargeOrder chargeOrder);

    List<ChargeOrder>getOrderListByExampleAndPage(JSONObject jsonParam);

    Integer getChargeOrderCount(JSONObject jsonParam);
}
