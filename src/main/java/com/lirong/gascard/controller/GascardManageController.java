package com.lirong.gascard.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.lirong.gascard.domain.Gascard;
import com.lirong.gascard.domain.GascardPrice;
import com.lirong.gascard.domain.OutPrice;
import com.lirong.gascard.domain.Users;
import com.lirong.gascard.service.GascardManageService;
import com.lirong.gascard.vo.CardAndOutprice;
import com.lirong.gascard.vo.CardAndPrice;
import com.lirong.gascard.vo.PageResultForBootstrap;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Handler;

/**
 * @Author: daimengying
 * @Date: 2018/5/29 11:05
 * @Description:油卡管理
 */
@Controller
@RequestMapping("/gascardManage")
public class GascardManageController extends BaseController{
    @Autowired
    GascardManageService gascardManageService;

    final


    /**
     * 油卡管理页面
     * @return
     */
    @RequestMapping("/toGasCard")
    public String toGasCard(HttpServletRequest req){
        menuTreeToSession(req);
        return "/gascardManage/cardTable";
    }

    /**
     * 报价管理页面
     * @return
     */
    @RequestMapping("/toCardPrice")
    public String toCardPrice(HttpServletRequest req){
        menuTreeToSession(req);
        return "/gascardManage/cardPriceTable";
    }

    /**
     * 外放价格管理
     */
    @RequestMapping("/toOutPrice")
    public String toOutPrice(HttpServletRequest req){
        menuTreeToSession(req);
        return "/gascardManage/outPriceTable";
    }

    /**
     * 油卡列表table
     * @param params
     * @return
     */
    @RequestMapping(value="/cardTable",method= RequestMethod.POST)
    @ResponseBody
    public PageResultForBootstrap cardTable(@RequestBody String params) {
        JSONObject parObject = JSON.parseObject(params);
        List<Gascard> gascardList=gascardManageService.getGascardsByExampleAndPage(parObject);
        //把油卡类型标识转为文字
        PageResultForBootstrap page = new PageResultForBootstrap();
        if(gascardList!=null && gascardList.size()>0){
            Gson gson =new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
            String gascardListString=gson.toJson(gascardList);
            List<Map<String,Object>> resultTable = gson.fromJson(gascardListString, new TypeToken<List<Map<String,String>>>() {}.getType());
            for(Map<String,Object> item:resultTable){
                switch(Integer.parseInt(item.get("type").toString())){
                    case 1:
                        item.put("type", "中国石油");
                        break;
                    case 2:
                        item.put("type", "中国石化");
                        break;
                }
            }
            page.setRows(resultTable);
            page.setTotal(gascardManageService.getGascardCountByExample(parObject));
        }
        return page;
    }

    /**
     * 报价表table
     * @param params
     * @return
     */
    @RequestMapping(value="/priceTable",method= RequestMethod.POST)
    @ResponseBody
    public PageResultForBootstrap priceTable(@RequestBody String params) {
        JSONObject parObject = JSON.parseObject(params);
        List<CardAndPrice>priceList=gascardManageService.getGascardPricesByExampleAndPage(parObject);
        //把油卡类型标识转为文字
        PageResultForBootstrap page = new PageResultForBootstrap();
        if(priceList!=null && priceList.size()>0){
            Gson gson =new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
            String priceListString=gson.toJson(priceList);
            List<Map<String,Object>> resultTable = gson.fromJson(priceListString, new TypeToken<List<Map<String,String>>>() {}.getType());
            for(Map<String,Object> item:resultTable){
                if(Integer.parseInt(item.get("type").toString())==1){
                    item.put("type", "中国石油");
                }else if(Integer.parseInt(item.get("type").toString())==2){
                    item.put("type", "中国石化");
                }
            }
            page.setRows(resultTable);
            page.setTotal(gascardManageService.getGascardPriceCountByExample(parObject));
        }
        return page;

    }

    @RequestMapping(value="/outPriceTable",method= RequestMethod.POST)
    @ResponseBody
    public PageResultForBootstrap outPriceTable(@RequestBody String params){
        JSONObject parObject = JSON.parseObject(params);
        List<CardAndOutprice> outpriceList= gascardManageService.getOutPriceByExampleAndPage(parObject);
        PageResultForBootstrap page = new PageResultForBootstrap();
        if(outpriceList!=null && outpriceList.size()>0){
            Gson gson =new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
            String outpriceListString=gson.toJson(outpriceList);
            List<Map<String,Object>> resultTable = gson.fromJson(outpriceListString, new TypeToken<List<Map<String,String>>>() {}.getType());
            for(Map<String,Object> item:resultTable){
                //油卡类型转换
                switch(Integer.parseInt(item.get("type")+"")){
                    case 1:
                        item.put("type", "中国石油");
                        break;
                    case 2:
                        item.put("type", "中国石化");
                        break;
                }
                //根据油卡ID获取最低报价
                GascardPrice lowest=gascardManageService.getLowestCardPrice(Integer.parseInt(item.get("gascardId")+""));
                item.put("lowestPrice", lowest.getPrice());
            }
            page.setRows(resultTable);
            page.setTotal(gascardManageService.getOutPriceCountByExample(parObject));
        }
        return page;
    }


    @RequestMapping("/toAddCard")
    public String toAddCard(){
        return "/gascardManage/addCard";
    }

    /**
     * 新增油卡种类
     * @param gascard
     * @return
     */
    @RequestMapping("/addCard")
    @ResponseBody
    public JSONObject addCard( @ModelAttribute Gascard gascard){
        JSONObject result = new JSONObject();
        Integer num=gascardManageService.addGascard(gascard);
        if(num>0){
            result.put("success", true);
        }else{
            result.put("success", false);
        }
        return result;
    }

    /**
     * 删除油卡种类
     * @param cardIds
     * @return
     */
    @RequestMapping("/deleteCard")
    @ResponseBody
    public JSONObject deleteCard( @RequestParam(value="cardIds",required=false) String[] cardIds){
        JSONObject result = new JSONObject();
        try{
            String[] returnCardIds=new String[cardIds.length];
            int j=0;
            for(int i=0;i<cardIds.length;i++){
                Integer deleteCard=gascardManageService.deleteGascard(Integer.parseInt(cardIds[i]+""));
                if(deleteCard>0){
                    returnCardIds[j]=cardIds[i]+"";
                    j++;
                }
            }
            if(returnCardIds.length>0&&returnCardIds!=null){
                result.put("success", true);
                result.put("data",returnCardIds.length);
            }else{
                result.put("success", false);
            }
        }catch (Exception e){
            result.put("success", false);
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 删除油卡报价记录
     * @param priceIds
     * @return
     */
    @RequestMapping("/deletePrice")
    @ResponseBody
    public JSONObject deletePrice( @RequestParam(value="priceIds",required=false) String[] priceIds){
        JSONObject result = new JSONObject();
        try{
            String[] returnPriceIds=new String[priceIds.length];
            int j=0;
            for(int i=0;i<priceIds.length;i++){
                Integer deletePrice=gascardManageService.deleteGascardPrice(Integer.parseInt(priceIds[i]+""));
                if(deletePrice>0){
                    returnPriceIds[j]=priceIds[i]+"";
                    j++;
                }
            }
            if(returnPriceIds.length>0&&returnPriceIds!=null){
                result.put("success", true);
                result.put("data",returnPriceIds.length);
            }else{
                result.put("success", false);
            }
        }catch (Exception e){
            result.put("success", false);
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 展示添加通道油卡报价
     * @return
     */
    @RequestMapping("/toAddCardPrice")
    public String toAddCardPrice(Model model, @RequestParam(value="cardPriceId",required=false)String cardPriceId){
        if(StrUtil.isNotBlank(cardPriceId)){
            //跳转编辑
            model.addAttribute("cardPriceInfo",gascardManageService.getCardPriceById(Integer.parseInt(cardPriceId)));
        }
        return "/gascardManage/addCardPrice";
    }

    /**
     * 根据油卡类型获取油卡列表，用作添加报价展示页面的二级联动
     * @param type
     * @return
     */
    @RequestMapping("/getPriceListByType")
    @ResponseBody
    public JSONObject getPriceListByType( @RequestParam(value="type") String type) {
        JSONObject result = new JSONObject();
        JSONObject parObject=new JSONObject();
        parObject.put("type",type);
        List<Gascard>cardList=gascardManageService.getGascardsByExampleAndPage(parObject);
        result.put("cardList", cardList);
        return result;
    }

    @RequestMapping("/addOrEditCardPrice")
    @ResponseBody
    public JSONObject addOrEditCardPrice( @ModelAttribute CardAndPrice cardAndPrice){
        JSONObject result = new JSONObject();
        Gascard cardInfo=gascardManageService.getCardInfoSelective(cardAndPrice.getType(),cardAndPrice.getAmount());
        cardAndPrice.setGascardId(cardInfo.getId());
        GascardPrice paramBean=new GascardPrice();
        BeanUtils.copyProperties(cardAndPrice,paramBean);
        Integer num=0;
        if(paramBean.getId()!=null&&paramBean.getId()>0){
            //更新。根据渠道和油卡ID获取报价表单条记录，若有记录则更新此记录的报价
            GascardPrice getCardPrice=gascardManageService.getOneCardPrice(cardInfo.getId(),cardAndPrice.getAccount());
            if(getCardPrice!=null){
                paramBean.setId(getCardPrice.getId());
            }
            num=gascardManageService.updateCardPriceByPK(paramBean);
        }else {
            num=gascardManageService.addGascardPrice(paramBean);
        }
        if(num>0){
            result.put("success", true);
        }else{
            result.put("success", false);
        }
        return result;
    }

    /**
     * 展示添加通道油卡报价
     * @return
     */
    @RequestMapping("/toAddOutPrice")
    public String toAddOutPrice(Model model, @RequestParam(value="outPriceId",required=false)String outPriceId){
        if(StrUtil.isNotBlank(outPriceId)){
            //跳转编辑
            model.addAttribute("outPriceInfo",gascardManageService.getCardOutByOutpriceId(Integer.parseInt(outPriceId)));
        }
        return "/gascardManage/addOutPrice";
    }

    /**
     * 根据面值和类型获取最低报价
     * @param type
     * @return
     */
    @RequestMapping("/getLowestPrice")
    @ResponseBody
    public GascardPrice getLowestPrice( @RequestParam(value="type") Integer type,@RequestParam(value="amount") Integer amount) {
        Gascard card=gascardManageService.getCardInfoSelective(type,amount);
        GascardPrice priceBean=gascardManageService.getLowestCardPrice(card.getId());
        return priceBean;
    }

    /**
     * 编辑或新增外放价格
     * @param cardAndOutprice
     * @return
     */
    @RequestMapping("/addOrEditOutPrice")
    @ResponseBody
    public JSONObject addOrEditOutPrice( @ModelAttribute CardAndOutprice cardAndOutprice){
        JSONObject result = new JSONObject();
        Gascard cardInfo=gascardManageService.getCardInfoSelective(cardAndOutprice.getType(),cardAndOutprice.getAmount());
        cardAndOutprice.setGascardId(cardInfo.getId());
        OutPrice outPrice=new OutPrice();
        BeanUtils.copyProperties(cardAndOutprice,outPrice);

        Integer num=0;
        if(cardAndOutprice.getId()!=null&&cardAndOutprice.getId()>0){
            //更新。根据代理商和油卡ID获取外放价格记录，若有记录则更新此记录的外放价格
            OutPrice getOutPrice=gascardManageService.getOneOutPrice(cardInfo.getId(),cardAndOutprice.getAccount());
            if(getOutPrice!=null){
                outPrice.setId(getOutPrice.getId());
            }
            num=gascardManageService.updateOutPriceByPK(outPrice);
        }else{
            num=gascardManageService.addOutPrice(outPrice);
        }
        if(num>0){
            result.put("success", true);
        }else{
            result.put("success", false);
            result.put("code", "-1002");
        }
        return result;
    }

    /**
     * 删除外放价格记录
     * @param outPrices
     * @return
     */
    @RequestMapping("/deleteOutPrice")
    @ResponseBody
    public JSONObject deleteOutPrice( @RequestParam(value="outPrices",required=false) String[] outPrices){
        JSONObject result = new JSONObject();
        try{
            String[] returnIds=new String[outPrices.length];
            int j=0;
            for(int i=0;i<outPrices.length;i++){
                Integer id=Integer.parseInt(outPrices[i]);
                Integer deletePrice=gascardManageService.deleteOutPrice(Integer.parseInt(outPrices[i]));
                if(deletePrice>0){
                    returnIds[j]=outPrices[i]+"";
                    j++;
                }
            }
            if(returnIds.length>0&&returnIds!=null){
                result.put("success", true);
                result.put("data",returnIds.length);
            }else{
                result.put("success", false);
            }
        }catch (Exception e){
            result.put("success", false);
            e.printStackTrace();
        }
        return result;
    }
}
