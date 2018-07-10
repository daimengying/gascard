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
 * @Date: 2018/5/17 16:55
 * @Description:
 */
@Table(name="g_loginfail_log")
public class LoginFailLog implements Serializable {

    @Transient
    private static final long serialVersionUID = 6863853221899102114L;

    @Id
    @GeneratedValue(generator = "JDBC")
    @Getter
    @Setter
    private Integer id;

    @Getter
    @Setter
    private String username;

    @Getter
    @Setter
    private String password;

    @Getter
    @Setter
    private String ip;

    @Getter
    @Setter
    private String browserType;//浏览器类型

    @Getter
    @Setter
    private String browserVersion;//浏览器版本

    @Getter
    @Setter
    private String platformType;//平台类型

    @Getter
    @Setter
    private String platformSeries;//平台系列

    @Getter
    @Setter
    private String platformVersion;//平台版本

    @Getter
    @Setter
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date loginTime;
}
