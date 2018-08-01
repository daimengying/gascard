package com.lirong.gascard.listener;

import com.lirong.gascard.domain.ScheduleJob;
import com.lirong.gascard.service.JobService;
import com.lirong.gascard.utils.ScheduleUtil;
import org.quartz.CronTrigger;
import org.quartz.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: daimengying
 * @Date: 2018/7/31 19:35
 * @Description:
 */
@Component
public class ScheduleListener implements CommandLineRunner {
    private  Logger _logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private JobService jobService;

    @Autowired
    private Scheduler scheduler;

    @Override
    public void run(String... args) throws Exception {
        // 应用启动之后执行所有可执行的的任务
        List<ScheduleJob> scheduleJobList = jobService.getAllEnableJob();
        for (ScheduleJob scheduleJob : scheduleJobList) {
            try {
                CronTrigger cronTrigger = ScheduleUtil.getCronTrigger(scheduler, scheduleJob);
                if (cronTrigger == null) {
                    ScheduleUtil.createScheduleJob(scheduler, scheduleJob);
                } else {
                    ScheduleUtil.updateScheduleJob(scheduler, scheduleJob);
                }
                _logger.info("Startup {}-{} success", scheduleJob.getJobGroup(), scheduleJob.getJobName());
            } catch (Exception e) {
                _logger.error("Startup error---"+e.getMessage());
            }
        }

    }
}
