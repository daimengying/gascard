package com.lirong.gascard.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: daimengying
 * @Date: 2018/5/29 16:45
 * @Description:油卡表和报价表关联实体类
 */
public class CardAndPrice  {



    @Getter
    @Setter
    private Integer id;//报价表ID

    @Getter
    @Setter
    private String account;//通道名称

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
    private Double price;//报价

    @Getter
    @Setter
    private Date optionTime;
}
