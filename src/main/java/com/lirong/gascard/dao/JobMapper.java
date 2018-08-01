package com.lirong.gascard.dao;

import com.lirong.gascard.config.BaseMapper;
import com.lirong.gascard.domain.ScheduleJob;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: daimengying
 * @Date: 2018/7/31 18:29
 * @Description:
 */
@Mapper
public interface JobMapper extends BaseMapper<ScheduleJob> {
}
