package com.lirong.gascard.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lirong.gascard.vo.UserAgent;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Author: daimengying
 * @Date: 2018/5/17 14:32
 * @Description:
 */
@Table(name="g_users")
public class Users implements Serializable {

    @Transient
    private static final long serialVersionUID = -2067704695739703581L;

    @Id
    @GeneratedValue(generator="JDBC")
    @Getter
    @Setter
    private  Integer id;

    @Getter
    @Setter
    private String username;

    @Getter
    @Setter
    private  String password;

    @Getter
    @Setter
    private Integer roleType;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private String phone;

    @Getter
    @Setter
    private String ipWhite;

    @Getter
    @Setter
    private String apiKey;

    @Getter
    @Setter
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @Getter
    @Setter
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date validateTime; //有效期

    @Getter
    @Setter
    private String agent;//添加代理商的管理员账号

    @Getter
    @Setter
    private Double balance;//余额

//    @Getter
//    @Setter
//    private Double preMoney;//预存款

    @Getter
    @Setter
    private Double creditFacility;//授信额度

    @Getter
    @Setter
    private Integer status;

    @Transient
    @Getter
    @Setter
    private UserAgent userAgent;





}
