package com.lirong.gascard.service;

import com.lirong.gascard.domain.ChannelLog;
import com.lirong.gascard.domain.ChannelParams;
import com.lirong.gascard.domain.ChargeOrder;
import lombok.NonNull;

/**
 * @Author: daimengying
 * @Date: 2018/6/7 08:51
 * @Description:分渠道提交订单到上游接口
 */
public interface SubmitOrderToChannelService {
    ChannelParams getOneChanelParams(@NonNull String channel);

    ChargeOrder submitToCachi(ChargeOrder chargeOrder);

    Integer addChannelLog(ChannelLog channelLog);
}
