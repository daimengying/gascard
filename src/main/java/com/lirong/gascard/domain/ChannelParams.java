package com.lirong.gascard.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author: daimengying
 * @Date: 2018/6/5 16:24
 * @Description:渠道参数表
 */
@Table(name="g_channel_params")
public class ChannelParams implements Serializable {

    @Transient
    private static final long serialVersionUID = 8063095337460767037L;

    @Id
    @GeneratedValue(generator = "JDBC")
    @Getter
    @Setter
    private Integer id;

    @Getter
    @Setter
    private String account;//渠道账户，与报价表的account一致

    @Getter
    @Setter
    private String url;//接口请求url

    @Getter
    @Setter
    private String params;//接口请求参数集

    @Getter
    @Setter
    private String queryUrl;//充值状态查询接口url

    @Getter
    @Setter
    private Integer status;

    @Getter
    @Setter
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date optionTime;


}
