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
 * @Date: 2018/6/1 18:13
 * @Description:外放价格表
 */
@Table(name="g_out_price")
public class OutPrice implements Serializable {

    private static final long serialVersionUID = 417454413619926864L;

    @Id
    @GeneratedValue(generator = "JDBC")
    @Getter
    @Setter
    private Integer id;

    @Getter
    @Setter
    private String account;//代理商账户

    @Getter
    @Setter
    private Integer gascardId;//油卡ID

    @Getter
    @Setter
    private Double outPrice;//外放价格

    @Getter
    @Setter
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date optionTime;

    @Getter
    @Setter
    private Integer status;
}
