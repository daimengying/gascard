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
 * @Date: 2018/6/6 18:00
 * @Description:回调日志表
 */
@Table(name="g_callback")
public class CallBack implements Serializable {

    private static final long serialVersionUID = 1012369992060420518L;

    @Id
    @GeneratedValue(generator="JDBC")
    @Getter
    @Setter
    private Integer id;

    @Getter
    @Setter
    private String account;

    @Getter
    @Setter
    private Integer orderId;

    @Getter
    @Setter
    private String cardnum;

    @Getter
    @Setter
    private String url;

    @Getter
    @Setter
    private String request;

    @Getter
    @Setter
    private String response;

    @Getter
    @Setter
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date optionTime;
}
