package com.lirong.gascard.service.impl;

import com.lirong.gascard.dao.JobMapper;
import com.lirong.gascard.domain.ScheduleJob;
import com.lirong.gascard.service.JobService;
import com.lirong.gascard.utils.ScheduleUtil;
import org.quartz.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: daimengying
 * @Date: 2018/7/31 18:34
 * @Description:
 */
@Service
public class JobServiceImpl implements JobService {
    private  Logger _logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private JobMapper jobMapper;
    @Autowired
    private Scheduler scheduler;


    @Override
    public List<ScheduleJob> getAllEnableJob() {
        Example example=new Example(ScheduleJob.class);
        Example.Criteria createCriteria = example.createCriteria();
        createCriteria.andEqualTo("enable",1);
        return jobMapper.selectByExample(example);
    }

    @Override
    public ScheduleJob select(Long jobId) {
        return jobMapper.selectByPrimaryKey(jobId);
    }

    @Override
    public ScheduleJob update(ScheduleJob scheduleJob) {
        Integer result= jobMapper.updateByPrimaryKeySelective(scheduleJob);
        if(result>0){
            ScheduleUtil.updateScheduleJob(scheduler, scheduleJob);
        }
        return scheduleJob;
    }

    @Override
    public boolean add(ScheduleJob scheduleJob) {
        Integer result= jobMapper.insertSelective(scheduleJob);
        if(result>0){
            ScheduleUtil.createScheduleJob(scheduler, scheduleJob);
            return true;
        }
        return false;
    }

    @Override
    public boolean delete(Long jobId) {
        ScheduleJob scheduleJob = select(jobId);
        if(scheduleJob!=null){
            Integer result= jobMapper.deleteByPrimaryKey(jobId);
            if(result>0){
                ScheduleUtil.deleteJob(scheduler, scheduleJob);
                return true;
            }
        }
        return false;
    }

    @Override
    public List<ScheduleJob> getAllJob() {
        return jobMapper.selectAll();
    }

    @Override
    public ScheduleJob updateScheduleJobStatus(Long jobId, Boolean isPause) {
        ScheduleJob scheduleJob = select(jobId);
        scheduleJob.setPause(isPause);
        update(scheduleJob);
        return scheduleJob;
    }

    @Override
    public boolean resume(Long jobId) {
        ScheduleJob scheduleJob = updateScheduleJobStatus(jobId, false);
        ScheduleUtil.resumeJob(scheduler, scheduleJob);
        return true;
    }

    @Override
    public boolean pause(Long jobId) {
        ScheduleJob scheduleJob = updateScheduleJobStatus(jobId, true);
        ScheduleUtil.pauseJob(scheduler, scheduleJob);
        return true;
    }

    @Override
    public boolean run(Long jobId) {
        ScheduleJob scheduleJob = updateScheduleJobStatus(jobId, false);
        ScheduleUtil.run(scheduler, scheduleJob);
        return true;
    }
}
