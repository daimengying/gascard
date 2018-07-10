package com.lirong.gascard.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;

/**
 * @Author: daimengying
 * @Date: 2018/5/17 15:14
 * @Description:
 */
@Table(name="g_menu")
public class Menu implements Serializable {
    @Transient
    private static final long serialVersionUID = 271613097636122096L;

    @Id
    @GeneratedValue(generator="JDBC")
    @Getter
    @Setter
    private Integer id;

    @Getter
    @Setter
    private Integer parentId;

    @Getter
    @Setter
    private String icon;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private String href;

    @Getter
    @Setter
    private Integer status;


}
