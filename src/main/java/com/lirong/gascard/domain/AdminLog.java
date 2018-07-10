package com.lirong.gascard.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author: daimengying
 * @Date: 2018/5/26 10:36
 * @Description:管理员操作日志表
 */
@Table(name="g_admin_log")
public class AdminLog implements Serializable {

    @Transient
    private static final long serialVersionUID = 3743521768928140671L;

    @Id
    @GeneratedValue(generator = "JDBC")
    @Getter
    @Setter
    private Integer id;

    @Getter
    @Setter
    private String account;

    @Getter
    @Setter
    private String title;

    @Getter
    @Setter
    private String desctext;

    @Getter
    @Setter
    private String ip;

    @Getter
    @Setter
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date optionTime;
}
