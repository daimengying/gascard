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
 * @Date: 2018/5/29 09:59
 * @Description:油卡种类表
 */
@Table(name="g_gascard")
public class Gascard implements Serializable {
    @Transient
    private static final long serialVersionUID = -5705843440582560117L;

    @Id
    @GeneratedValue(generator = "JDBC")
    @Getter
    @Setter
    private Integer id;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private Integer amount;//面值

    @Getter
    @Setter
    private Integer type;//1：中石油 2：中石化

    @Getter
    @Setter
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date optionTime;

    @Getter
    @Setter
    private Integer status;
}
