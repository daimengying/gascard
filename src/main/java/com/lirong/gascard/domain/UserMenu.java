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
 * @Date: 2018/5/17 15:36
 * @Description:
 */
@Table(name="g_user_menu")
public class UserMenu implements Serializable {

    @Transient
    private static final long serialVersionUID = -6584038431972060414L;

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
    private Integer menuId;

    @Transient
    @Getter
    @Setter
    private Integer parentId;


}
