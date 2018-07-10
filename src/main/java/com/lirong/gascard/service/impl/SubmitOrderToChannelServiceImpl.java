package com.lirong.gascard.service.impl;

import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lirong.gascard.dao.ChannelLogMapper;
import com.lirong.gascard.dao.ChannelParamsMapper;
import com.lirong.gascard.dao.ChargeOrderMapper;
import com.lirong.gascard.domain.ChannelLog;
import com.lirong.gascard.domain.ChannelParams;
import com.lirong.gascard.domain.ChargeOrder;
import com.lirong.gascard.service.SubmitOrderToChannelService;
import com.lirong.gascard.utils.RestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;

/**
 * @Author: daimengying
 * @Date: 2018/6/7 08:58
 * @Description:分渠道提交订单到上游逻辑
 */
@Service
public class SubmitOrderToChannelServiceImpl implements SubmitOrderToChannelService {
    private Logger _logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    ChargeOrderMapper chargeOrderMapper;
    @Resource
    ChannelParamsMapper channelParamsMapper;
    @Resource
    ChannelLogMapper channelLogMapper;
    @Resource
    private RestUtils restUtil;

    /**
     * 提交到卡池通道
     * @param chargeOrder
     */
    @Override
    public ChargeOrder submitToCachi(ChargeOrder chargeOrder) {
        try {
            //根据渠道获取参数
            String channel=chargeOrder.getChannel();
            ChannelParams channelParams=this.getOneChanelParams(channel);
            JSONObject params= JSON.parseObject(channelParams.getParams());
            String url=channelParams.getUrl();

            String sign=params.getString("key");
            String userid=params.getString("userid");
            String mobile=chargeOrder.getCardnum();
            String productid= getProductId(params.getString("productid"),chargeOrder.getAmount(),chargeOrder.getType());
            if(StringUtils.isEmpty(productid)){
                chargeOrder.setChargeStatus(3);
                chargeOrder.setMemo("未找到商品productId");
            }else{
                //MD5加密串MD5.digest(userid + mobile + productid+sign)
                String key= DigestUtil.md5Hex(userid + mobile + productid+sign);

                //将params中参数重新赋值作为接口入参
                params.put("mobile", chargeOrder.getCardnum());//卡号
                params.put("bizid", chargeOrder.getUpOrderId());//订单ID
                params.put("productid", productid);//商品ID
                params.put("key", key);//数字签名
                String restCallResult=restUtil.restCallExchange(    url,params);
                //异步记录提单到上游接口日志
                ChannelLog log=new ChannelLog();
                log.setCardnum(mobile);
                log.setChannel(channel);
                log.setOrderId(chargeOrder.getUpOrderId());
                log.setRequest(url+">>>"+ JSON.toJSONString(params));
                log.setResponse(restCallResult);
                this.addChannelLog(log);
                JSONObject resultObj=JSONObject.parseObject(restCallResult);
                //此处接口调用成功不代表就充值成功，只有在接收到上游notify的时候才能判断
                if("T00001".equals(resultObj.getString("resultCode"))){
                    chargeOrder.setChargeStatus(2);
                    chargeOrder.setUpReturnOrderid(resultObj.getString("resultMsg"));
                }else{
                    chargeOrder.setChargeStatus(3);
                    chargeOrder.setMemo("提单到上游失败");
                }
            }
            chargeOrderMapper.updateByPrimaryKeySelective(chargeOrder);
        }catch (Exception e){
            _logger.error("【充值模块】订单提交到上游异常，错误信息：" + e.getMessage());
        }
        return chargeOrder;
    }

    /**
     * 获取渠道参数
     * @param channel
     * @return
     */
    @Override
    public ChannelParams getOneChanelParams(String channel) {
        Example example = new Example(ChannelParams.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("account",channel);
        criteria.andEqualTo("status",0);
        return channelParamsMapper.selectOneByExample(example);
    }

    @Async
    @Override
    public Integer addChannelLog(ChannelLog channelLog) {
        return channelLogMapper.insertSelective(channelLog);
    }

    /**
     * 解析ChannelParams中的produceIds
     * 类型：面值：商品ID  (type:amount:produceid,)...
     * 如：1:50:00401010050,
     * @param productIds
     * @return
     */
    public String getProductId(String productIds,Integer amount,Integer type){
        String[] amountAndProIds=productIds.split(",");
        String productid="";
        for(String item:amountAndProIds){
            String itemType=item.split(":")[0];
            String itemAmount=item.split(":")[1];
            String itemProId=item.split(":")[2];
            if(Integer.parseInt(itemType)==type && Integer.parseInt(itemAmount)==amount){
                productid=itemProId;
                break;
            }
        }
        return productid;
    }
}
