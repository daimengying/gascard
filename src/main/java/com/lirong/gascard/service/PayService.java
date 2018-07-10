package com.lirong.gascard.service;

import com.alibaba.fastjson.JSONObject;
import com.lirong.gascard.domain.AgentChargeLog;
import com.lirong.gascard.domain.PayLog;

import java.util.List;
import java.util.Map;

/**
 * @Author: daimengying
 * @Date: 2018/5/26 16:55
 * @Description:消费
 */
public interface PayService {
    Integer addPay(PayLog payLog);

    Integer addAgentChargeLog(AgentChargeLog agentChargeLog);

    List<AgentChargeLog> getAgentChargeLogListByExampleAndPage(JSONObject jsonParam);

    Integer getAgentChargeLogCount(JSONObject jsonParam);

    List<PayLog>getPayLogListByExampleAndPage(JSONObject jsonParam);

    Integer getPayLogCount(JSONObject jsonParam);
}
