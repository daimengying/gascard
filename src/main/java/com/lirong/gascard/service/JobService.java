package com.lirong.gascard.service;

import com.lirong.gascard.domain.ScheduleJob;

import java.util.List;

/**
 * @Author: daimengying
 * @Date: 2018/7/31 18:30
 * @Description:
 */
public interface JobService {
    List<ScheduleJob> getAllEnableJob();

    ScheduleJob select(Long jobId);

    ScheduleJob update(ScheduleJob scheduleJob);

    boolean add(ScheduleJob scheduleJob);

    boolean delete(Long jobId);

    List<ScheduleJob> getAllJob();

    boolean resume(Long jobId);

    boolean pause(Long jobId);

    boolean run(Long jobId);

    ScheduleJob updateScheduleJobStatus(Long jobId, Boolean isPause);

}
