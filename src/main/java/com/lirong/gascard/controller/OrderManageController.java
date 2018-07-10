package com.lirong.gascard.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.lirong.gascard.domain.ChargeOrder;
import com.lirong.gascard.domain.PayLog;
import com.lirong.gascard.domain.Users;
import com.lirong.gascard.service.ChargeOrderService;
import com.lirong.gascard.service.PayService;
import com.lirong.gascard.vo.PageResultForBootstrap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @Author: daimengying
 * @Date: 2018/5/28 16:03
 * @Description:订单管理模块
 */
@Controller
@RequestMapping("/orderManage")
public class OrderManageController extends  BaseController{
    @Autowired
    PayService payService;
    @Autowired
    ChargeOrderService chargeOrderService;

    /**
     * 消费明细表格
     * @return
     */
    @RequestMapping("/toConsumeDatail")
    public String toConsumeDatail(HttpServletRequest req){
        menuTreeToSession(req);
        return "/orderManage/consumeDetail";
    }

    /**
     * 充值记录表
     * @return
     */
    @RequestMapping("/toChargeRecord")
    public String toChargeRecord(HttpServletRequest req){
        menuTreeToSession(req);
        return "/orderManage/chargeRecord";
    }

    @RequestMapping(value="/consumeDetailTable",method= RequestMethod.POST)
    @ResponseBody
    public PageResultForBootstrap consumeDetailTable(HttpServletRequest request, @RequestBody String params){
        Users currentUser =getCurrentUser(request);
        JSONObject parObject= JSON.parseObject(params);
        parObject.put("roleType",currentUser.getRoleType());
        parObject.put("loginAccount",currentUser.getUsername());
        List<PayLog>payLogList=payService.getPayLogListByExampleAndPage(parObject);
        //把消费类型标识转为文字
        PageResultForBootstrap page = new PageResultForBootstrap();
        if(payLogList!=null && payLogList.size()>0){
            Gson gson =new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
            String payLogListTableString=gson.toJson(payLogList);
            List<Map<String,Object>> resultTable = gson.fromJson(payLogListTableString, new TypeToken<List<Map<String,String>>>() {}.getType());
            for(Map<String,Object> item:resultTable){
                item.put("money", Double.parseDouble(item.get("money").toString()));
                item.put("balance", Double.parseDouble(item.get("balance").toString()));
                switch(Integer.parseInt(item.get("type").toString())){
                    case 1:
                        item.put("type", "冲扣值");
                        break;
                    case 2:
                        item.put("type", "充值扣费");
                        break;
                    case 3:
                        item.put("type", "失败退款");
                        break;
                }
            }
            page.setRows(resultTable);
            page.setTotal(payService.getPayLogCount(parObject));
        }
        return page;
    }

    @RequestMapping(value="/chargeRecordTable",method= RequestMethod.POST)
    @ResponseBody
    public PageResultForBootstrap chargeRecordTable(HttpServletRequest request, @RequestBody String params){
        Users currentUser =getCurrentUser(request);
        JSONObject parObject= JSON.parseObject(params);
        parObject.put("roleType",currentUser.getRoleType());
        parObject.put("loginAccount",currentUser.getUsername());
        PageResultForBootstrap page = new PageResultForBootstrap();
        List<ChargeOrder>chargeOrderList=chargeOrderService.getOrderListByExampleAndPage(parObject);
        page.setRows(chargeOrderList);
        page.setTotal(chargeOrderService.getChargeOrderCount(parObject));
        return page;
    }


}
