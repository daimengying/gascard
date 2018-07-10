package com.lirong.gascard.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author: daimengying
 * @Date: 2018/5/26 12:20
 * @Description: 代理商充值记录
 */
@Table(name="g_agent_charge_log")
public class AgentChargeLog implements Serializable {


    private static final long serialVersionUID = -4766809977831114556L;

    @Id
    @GeneratedValue(generator="JDBC")
    @Getter
    @Setter
    private Integer id;

    @Getter
    @Setter
    private Integer userId;

    @Getter
    @Setter
    private String account;//代理商账户

    @Getter
    @Setter
    private String agent;

    @Getter
    @Setter
    private Double money;

    @Getter
    @Setter
    private Integer payType;//1：公账收款 2：公支付宝 3：私账 4：转移 5：未到账退款 6：测试加款 7：其他原因充扣款

    @Getter
    @Setter
    private String optionUser;

    @Getter
    @Setter
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date optionTime;

    @Getter
    @Setter
    private String memo;

    @Getter
    @Setter
    private Integer status;
}
