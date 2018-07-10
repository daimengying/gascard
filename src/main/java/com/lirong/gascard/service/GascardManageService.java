package com.lirong.gascard.service;

import com.alibaba.fastjson.JSONObject;
import com.lirong.gascard.domain.Gascard;
import com.lirong.gascard.domain.GascardPrice;
import com.lirong.gascard.domain.OutPrice;
import com.lirong.gascard.vo.CardAndOutprice;
import com.lirong.gascard.vo.CardAndPrice;
import lombok.NonNull;

import java.util.List;

/**
 * @Author: daimengying
 * @Date: 2018/5/29 10:15
 * @Description:油卡管理
 */
public interface GascardManageService {
    List<Gascard>getGascardsByExampleAndPage(JSONObject jsonParam);

    Integer getGascardCountByExample(JSONObject jsonParam);

    List<CardAndPrice>getGascardPricesByExampleAndPage(JSONObject jsonParam);

    Integer getGascardPriceCountByExample(JSONObject jsonParam);

    Integer addGascard(Gascard gascard);

    Integer addGascardPrice(GascardPrice gascardPrice);

    Integer updateCardPriceByPK(GascardPrice gascardPrice);

    Integer deleteGascard(@NonNull Integer cardId);

    Integer deleteGascardPrice(@NonNull Integer cardPriceId);

    CardAndPrice getCardPriceById(@NonNull Integer id);

    Gascard getCardInfoSelective(@NonNull Integer type,@NonNull Integer amount);

    List<CardAndOutprice>getOutPriceByExampleAndPage(JSONObject jsonParam);

    Integer getOutPriceCountByExample(JSONObject jsonParam);

    Integer addOutPrice(OutPrice outPrice);

    Integer deleteOutPrice(@NonNull Integer id);

    OutPrice getOneOutPrice(@NonNull Integer cardId,@NonNull String account);

    GascardPrice getOneCardPrice(@NonNull Integer cardId,@NonNull String account);

    CardAndOutprice getCardOutByOutpriceId(@NonNull Integer id);

    GascardPrice getLowestCardPrice(@NonNull Integer cardId);

    Integer updateOutPriceByPK(OutPrice outPrice);
}
