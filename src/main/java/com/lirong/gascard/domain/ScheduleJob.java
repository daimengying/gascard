package com.lirong.gascard.domain;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author: daimengying
 * @Date: 2018/7/31 16:05
 * @Description:自定义管理定时任务实体类
 */
@Table(name="g_job")
@Data
public class ScheduleJob implements Serializable {

    private static final long serialVersionUID = -674953454335065358L;

    @Id
    @GeneratedValue(generator = "JDBC")
    private Long id;

    private String className;

    private String cronExpression;

    private String jobName;

    private String jobGroup;

    private String triggerName;

    private String triggerGroup;

    private Boolean pause;

    private Boolean enable;

    private String description;

    private Date createTime;

    private Date lastUpdateTime;

}
