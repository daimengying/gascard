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
 * @Date: 2018/6/7 10:10
 * @Description:
 */
@Table(name="g_channel_log")
public class ChannelLog implements Serializable {
    private static final long serialVersionUID = -1687810157676209663L;

    @Id
    @GeneratedValue(generator="JDBC")
    @Getter
    @Setter
    private Integer id;

    @Getter
    @Setter
    private String orderId;

    @Getter
    @Setter
    private String channel;

    @Getter
    @Setter
    private String cardnum;

    @Getter
    @Setter
    private String request;

    @Getter
    @Setter
    private String response;

    @Getter
    @Setter
    private Integer result;

    @Getter
    @Setter
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date optionTime;
}
