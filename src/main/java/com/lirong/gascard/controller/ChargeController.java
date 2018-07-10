package com.lirong.gascard.controller;

import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lirong.gascard.domain.*;
import com.lirong.gascard.service.ChargeOrderService;
import com.lirong.gascard.service.GascardManageService;
import com.lirong.gascard.service.LoginService;
import org.apache.catalina.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import sun.applet.Main;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author: daimengying
 * @Date: 2018/6/1 13:46
 * @Description:油卡充值接口，供下游调用
 */
@EnableAsync
@Controller
@RequestMapping("/charge")
public class ChargeController  {
    private Logger _logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    LoginService loginService;

    @Autowired
    GascardManageService gascardManageService;

    @Autowired
    ChargeOrderService chargeOrderService;
    /**
     * 下游调用我方充值接口
     * @param cardnum
     * @param type
     * @param amount
     * @param orderId
     * @param sign
     * @return
     */
    @RequestMapping(value="/chargeLogic",method= RequestMethod.POST,produces="application/json;charset=UTF-8")
    @ResponseBody
    public String chargeLogic(String username,String cardnum,Integer type,Integer amount,String orderId,String sign,String backUrl){
        _logger.info("【/charge/chargeLogic】 充值接口入参：username={},cardnum={},type={},amount={},orderId={},sign={},backUrl={}",
                username,cardnum,type,amount,orderId,sign,backUrl);
        JSONObject result = new JSONObject();
        if(StringUtils.isEmpty(username)||StringUtils.isEmpty(cardnum)
                ||type==null||amount==null||StringUtils.isEmpty(orderId)){
            result.put("success",false);
            result.put("data","参数不完整");
            return JSON.toJSONString(result);
        }
        List<Users>agents=loginService.getUserByUserName(username);
        if ((agents == null) || (agents.get(0).getRoleType() != 3)) {
            result.put("success",false);
            result.put("data","账号不存在");
            return JSON.toJSONString(result);
        }
        Users agent=agents.get(0);
        //根据卡号和类型验证油卡是否匹配。石油"9"开头16位，石化"1"开头19位
        if(type==1){
            if(!cardnum.startsWith("9")||cardnum.length()!=16){
                result.put("success",false);
                result.put("data","非中石油油卡");
                return JSON.toJSONString(result);
            }
        }else if(type==2){
            if(!cardnum.startsWith("1")||cardnum.length()!=19){
                result.put("success",false);
                result.put("data","非中石化油卡");
                return JSON.toJSONString(result);
            }
        }else{
            result.put("success",false);
            result.put("data","油卡类型错误");
            return JSON.toJSONString(result);
        }
        //根据油卡类型获取油卡列表，判断面值是否在其中
        JSONObject parObject=new JSONObject();
        parObject.put("type",type);
        List<Gascard>cardList=gascardManageService.getGascardsByExampleAndPage(parObject);
        boolean flag=false;
        for(Gascard item:cardList){
            if(amount==item.getAmount()){
                flag=true;
                break;
            }
        }
        if(!flag){
            String typeName=type==1?"中石油":"中石化";
            result.put("success",false);
            result.put("data","不支持面值"+amount+"元的"+typeName+"加油卡");
            return JSON.toJSONString(result);
        }
        //username,cardnum,type,amount,apikey 参与签名
        StringBuffer resign = new StringBuffer();
        resign.append("username=").append(username);
        resign.append("&cardnum=").append(cardnum);
        resign.append("&type=").append(type);
        resign.append("&amount=").append(amount);
        resign.append("&key=").append(agent.getApiKey());
        String md5sign = DigestUtil.md5Hex(resign.toString());
        if (!md5sign.equals(sign.toLowerCase())) {
            result.put("success",false);
            result.put("data","签名不正确");
            return JSON.toJSONString(result);
        }
        if (agent.getStatus() == -1) {
            result.put("success",false);
            result.put("data","用户被锁定！");
            return JSON.toJSONString(result);
        }
        if (agent.getValidateTime().getTime() < System.currentTimeMillis()) {
            result.put("success",false);
            result.put("data","用户已过有效期！");
            return JSON.toJSONString(result);
        }
        //校验单号是否重复
        int num=chargeOrderService.verifyOrderId(username,orderId);
        if(num>1){
            result.put("success",false);
            result.put("data","订单号重复");
            return JSON.toJSONString(result);
        }
        //异步事件。订单记录入库
        String upOrderId=username+new Date().getTime();
        ChargeOrder chargeOrder=new ChargeOrder();
        chargeOrder.setAccount(username);
        chargeOrder.setAmount(amount);
        chargeOrder.setCardnum(cardnum);
        chargeOrder.setOrderId(orderId);
        chargeOrder.setUpOrderId(upOrderId);
        chargeOrder.setType(type);
        chargeOrder.setBackUrl(backUrl);

        //校验是否有该油卡，以及是否设置最低成本
        Gascard cardInfo = gascardManageService.getCardInfoSelective(type,amount);
        GascardPrice gascardPrice=gascardManageService.getLowestCardPrice(cardInfo.getId());
        if(cardInfo==null || gascardPrice==null){
            chargeOrder.setMemo("此油卡暂未开放");
            chargeOrder.setChargeStatus(3);
            chargeOrderService.addOrder(chargeOrder);
            result.put("success",false);
            result.put("data","提交失败");
            return JSON.toJSONString(result);
        }

        chargeOrder.setDiscountPrice(gascardPrice.getPrice());
        chargeOrder.setChannel(gascardPrice.getAccount());

        //根据油卡ID校验是否给该代理商设置了外放价格
        OutPrice outPriceInfo=gascardManageService.getOneOutPrice(cardInfo.getId(),username);
        if(outPriceInfo==null){
            chargeOrder.setMemo("此油卡暂未设置对外价格");
            chargeOrder.setChargeStatus(3);
            chargeOrderService.addOrder(chargeOrder);
            result.put("success",false);
            result.put("data","提交失败");
            return JSON.toJSONString(result);
        }
        chargeOrder.setChargePrice(outPriceInfo.getOutPrice());
        //校验外放价格是否低于最低成本价
        if(outPriceInfo.getOutPrice()<gascardPrice.getPrice()){
            chargeOrder.setMemo("充值价格设置过低");
            chargeOrder.setChargeStatus(3);
            chargeOrderService.addOrder(chargeOrder);
            result.put("success",false);
            result.put("data","提交失败");
            return JSON.toJSONString(result);
        }
        //校验代理商余额是否不足
        if((agent.getBalance()+agent.getCreditFacility()) < outPriceInfo.getOutPrice()){
            chargeOrder.setMemo("余额不足");
            chargeOrder.setChargeStatus(3);
            chargeOrderService.addOrder(chargeOrder);
            result.put("success",false);
            result.put("data","提交失败");
            return JSON.toJSONString(result);
        }
        chargeOrder.setMemo("提交成功");
        chargeOrder.setChargeStatus(2);
        chargeOrder.setProfit(outPriceInfo.getOutPrice()-gascardPrice.getPrice());
        //记录订单到数据库
        chargeOrderService.addOrder(chargeOrder);

        //异步提单到上游。
        chargeOrderService.submitOrderToUp(chargeOrder);

        //返回给代理商的结果
        JSONObject resultData=new JSONObject();
        resultData.put("msg","提交成功");
        resultData.put("orderId",orderId);
        resultData.put("taskId",upOrderId);
        result.put("data",resultData);
        result.put("success",true);
        return JSON.toJSONString(result);
    }


    /**
     * 对外提供充值状态查询接口
     * @param username
     * @param orderId
     * @param sign
     * @return
     */
    @RequestMapping(value="/getOrderStatus",method=RequestMethod.POST)
    @ResponseBody
    public String getOrderStatus( String username, String orderId, String sign) {
        JSONObject result = new JSONObject();
        List<Users>agents=loginService.getUserByUserName(username);
        if ((agents == null) || (agents.get(0).getRoleType() != 3)) {
            result.put("success",false);
            result.put("data","账号不存在");
            return JSON.toJSONString(result);
        }
        Users agent=agents.get(0);
        //account,orderId,apikey 参与签名
        StringBuffer resign = new StringBuffer();
        resign.append("username=").append(username);
        resign.append("&orderId=").append(orderId);
        resign.append("&key=").append(agent.getApiKey());
        String md5sign = DigestUtil.md5Hex(resign.toString());
        if (!md5sign.equals(sign.toLowerCase())) {
            result.put("success",false);
            result.put("data","签名不正确");
            return JSON.toJSONString(result);
        }
        ChargeOrder order = chargeOrderService.getOneChargeOrder(username,orderId);
        if(null == order){
            result.put("success",false);
            result.put("msg","订单不存在");
        }else {
            result.put("success", true);
            result.put("msg", "查询成功");
            JSONObject data=new JSONObject();
            data.put("cardnum",order.getCardnum());
            data.put("orderId",order.getOrderId());
            data.put("taskId",order.getUpOrderId());
            data.put("reportTime",order.getReportTime());
            data.put("orderStatus",order.getChargeStatus());
            result.put("data", data);
        }
        return JSON.toJSONString(result);
    }

}
