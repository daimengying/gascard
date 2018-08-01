package com.lirong.gascard.job;

import com.lirong.gascard.service.JobService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @Author: daimengying
 * @Date: 2018/7/31 19:38
 * @Description:任务类
 */
@Component
public class DateJob implements Job {
    private Logger _logger = LoggerFactory.getLogger(this.getClass());

    // 如果没有自定义改写 JobFactory，这里会注入失败
    @Autowired
    private JobService jobService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        try {
//            jobService.getAllJob();
            System.out.println("当前时间----"+new Date());
        } catch (Exception e) {
            _logger.error("Parse announcement failed, error message is {}", e.getMessage());
        }
    }
}
