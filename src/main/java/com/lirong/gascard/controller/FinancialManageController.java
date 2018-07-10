package com.lirong.gascard.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.lirong.gascard.domain.AgentChargeLog;
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
 * @Date: 2018/5/28 09:43
 * @Description:财务管理
 */
@Controller
@RequestMapping("/financialManage")
public class FinancialManageController extends BaseController {

    @Autowired
    PayService payService;

    /**
     * 财务管理页面
     * @return
     */
    @RequestMapping("/toFinancialManage")
    public String toFinancialManage(HttpServletRequest req){
        menuTreeToSession(req);
        return "/financialManage/financialManage";
    }

    @RequestMapping(value="/financialManageTable",method= RequestMethod.POST)
    @ResponseBody
    public PageResultForBootstrap financialManageTable(@RequestBody String params){
        JSONObject parObject= JSON.parseObject(params);
        List<AgentChargeLog>agentChargeLogTable=payService.getAgentChargeLogListByExampleAndPage(parObject);
        //把支付类型标识转为文字
        PageResultForBootstrap page = new PageResultForBootstrap();
        if(agentChargeLogTable!=null && agentChargeLogTable.size()>0){
            Gson gson =new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
            String agentChargeLogTableString=gson.toJson(agentChargeLogTable);
            List<Map<String,Object>> resultTable = gson.fromJson(agentChargeLogTableString, new TypeToken<List<Map<String,String>>>() {}.getType());
            for(Map<String,Object> item:resultTable){
                item.put("money", Double.parseDouble(item.get("money").toString()));
                switch(Integer.parseInt(item.get("payType").toString())){
                    case 1:
                        item.put("payType", "公账收款");
                        break;
                    case 2:
                        item.put("payType", "公支付宝");
                        break;
                    case 3:
                        item.put("payType", "私账");
                        break;
                    case 4:
                        item.put("payType", "转移");
                        break;
                    case 5:
                        item.put("payType", "未到账退款");
                        break;
                    case 6:
                        item.put("payType", "测试加款");
                        break;
                    case 7:
                        item.put("payType", "其他原因充扣款");
                        break;
                }
            }
            page.setRows(resultTable);
            page.setTotal(payService.getAgentChargeLogCount(parObject));
        }
        return page;

    }

}
