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
 * @Date: 2018/5/26 16:50
 * @Description:消费明细表
 */
@Table(name="g_pay_log")
public class PayLog implements Serializable {

    @Transient
    private static final long serialVersionUID = -7006768316819511339L;

    @Id
    @GeneratedValue(generator="JDBC")
    @Getter
    @Setter
    private Integer id;

    @Getter
    @Setter
    private Integer userId;//用户

    @Getter
    @Setter
    private String account;//用户账户

    @Getter
    @Setter
    private String agent;//代理商等级

    @Getter
    @Setter
    private Double money;//扣费金额

    @Getter
    @Setter
    private Double balance;//余额

    @Getter
    @Setter
    private Integer type;//扣费类型   1 冲扣值  2 充值扣费  3 失败退款

    @Getter
    @Setter
    private String memo;//备注

    @Getter
    @Setter
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date optionTime;//扣费时间

    @Getter
    @Setter
    private Integer status ;
}
