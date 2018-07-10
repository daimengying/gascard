package com.lirong.gascard.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @Author: daimengying
 * @Date: 2018/6/4 11:10
 * @Description:油卡表和外放价格表实体类
 */
public class CardAndOutprice {
    @Getter
    @Setter
    private Integer id;//外放价格表ID

    @Getter
    @Setter
    private String account;//代理商账户

    @Getter
    @Setter
    private String name;//油卡名称

    @Getter
    @Setter
    private Integer type;//油卡类型

    @Getter
    @Setter
    private Integer amount;//油卡面值

    @Getter
    @Setter
    private Integer gascardId;//油卡表ID

    @Getter
    @Setter
    private Double outPrice;//外放价格

    @Getter
    @Setter
    private Date optionTime;
}
