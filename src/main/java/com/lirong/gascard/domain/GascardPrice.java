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
 * @Date: 2018/5/29 09:56
 * @Description:油卡报价表g_gascard_price
 */
@Table(name="g_gascard_price")
public class GascardPrice implements Serializable {
    @Transient
    private static final long serialVersionUID = -4118257362205290838L;

    @Id
    @GeneratedValue(generator = "JDBC")
    @Getter
    @Setter
    private Integer id;

    @Getter
    @Setter
    private String account;//上游账户

    @Getter
    @Setter
    private Integer gascardId;

    @Getter
    @Setter
    private Double price;

    @Getter
    @Setter
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date optionTime;

    @Getter
    @Setter
    private Integer status;
}
