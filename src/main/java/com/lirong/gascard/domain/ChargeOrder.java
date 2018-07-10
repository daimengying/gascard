package com.lirong.gascard.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author: daimengying
 * @Date: 2018/6/1 15:53
 * @Description:订单表
 */
@Table(name="g_charge_order")
public class ChargeOrder implements Serializable {

    private static final long serialVersionUID = -4084323991063177279L;

    @Id
    @GeneratedValue(generator="JDBC")
    @Getter
    @Setter
    private Integer id;

    @Getter
    @Setter
    private String account;//代理商账号

    @Getter
    @Setter
    private String cardnum;//卡号

    @Getter
    @Setter
    private Integer type;//油卡类型   1 石油 2石化

    @Getter
    @Setter
    private Integer amount;//面值

    @Getter
    @Setter
    private String channel;//充值通道，上游账户

    @Getter
    @Setter
    private Double chargePrice;//外放价格

    @Getter
    @Setter
    private Double discountPrice;//成本

    @Getter
    @Setter
    private Double profit;//利润

    @Getter
    @Setter
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date optionTime;//提交时间

    @Getter
    @Setter
    private String orderId;//下游传来的订单号

    @Getter
    @Setter
    private String backUrl;//下游提供的充值结果回调地址

    @Getter
    @Setter
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date reportTime;//上游给回调的时间

    @Getter
    @Setter
    private String upOrderId;//给上游的订单号

    @Getter
    @Setter
    private String upReturnOrderid;//充值成功后上游返回的订单ID

    @Getter
    @Setter
    private Integer chargeStatus;//充值状态  1 未知  2 提交成功  3 提交失败  4 充值成功  5 充值失败

    @Getter
    @Setter
    private String memo;//备注




}
