package com.lirong.gascard.utils;

import com.lirong.gascard.domain.ScheduleJob;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author: daimengying
 * @Date: 2018/7/31 16:07
 * @Description:定时器工具类
 */
public class ScheduleUtil {

    private final static Logger _logger = LoggerFactory.getLogger(ScheduleUtil.class);

    /**
     * 获取 Trigger Key
     *
     * @param scheduleJob
     * @return
     */
    public static TriggerKey getTriggerKey(ScheduleJob scheduleJob) {
        return TriggerKey.triggerKey(scheduleJob.getTriggerName(), scheduleJob.getTriggerGroup());
    }

    /**
     * 获取 Job Key
     *
     * @param scheduleJob
     * @return
     */
    public static JobKey getJobKey(ScheduleJob scheduleJob) {
        return JobKey.jobKey(scheduleJob.getJobName(), scheduleJob.getJobGroup());
    }

    /**
     * 获取 Cron Trigger
     *
     * @param scheduler
     * @param scheduleJob
     * @return
     */
    public static CronTrigger getCronTrigger(Scheduler scheduler, ScheduleJob scheduleJob) {
        try {
            return (CronTrigger) scheduler.getTrigger(getTriggerKey(scheduleJob));
        } catch (SchedulerException e) {
            _logger.error("Get Cron trigger failed，错误信息：" + e.getMessage());
            return null;
        }
    }

    /**
     * 创建任务
     *
     * @param scheduler
     * @param scheduleJob
     */
    public static void createScheduleJob(Scheduler scheduler, ScheduleJob scheduleJob)  {

        validateCronExpression(scheduleJob);

        try {
            // 要执行的 Job 的类
            Class<? extends Job> jobClass = (Class<? extends Job>) Class.forName(scheduleJob.getClassName()).newInstance().getClass();

            JobDetail jobDetail = JobBuilder.newJob(jobClass)
                    .withIdentity(scheduleJob.getJobName(), scheduleJob.getJobGroup())
                    .withDescription(scheduleJob.getDescription())
                    .build();

            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(scheduleJob.getCronExpression())
                    .withMisfireHandlingInstructionDoNothing();

            CronTrigger cronTrigger = TriggerBuilder.newTrigger()
                    .withIdentity(scheduleJob.getTriggerName(), scheduleJob.getTriggerGroup())
                    .withDescription(scheduleJob.getDescription())
                    .withSchedule(scheduleBuilder)
                    .startNow()
                    .build();

            scheduler.scheduleJob(jobDetail, cronTrigger);

            _logger.info("Create schedule job {}-{} success", scheduleJob.getJobGroup(), scheduleJob.getJobName());

            if (scheduleJob.getPause()) {
                pauseJob(scheduler, scheduleJob);
            }
        } catch (Exception e) {
            _logger.error("Execute schedule job failed"+e.getMessage());
        }
    }


    /**
     * 更新任务
     *
     * @param scheduler
     * @param scheduleJob
     */
    public static void updateScheduleJob(Scheduler scheduler, ScheduleJob scheduleJob)  {

        validateCronExpression(scheduleJob);

        try {

            TriggerKey triggerKey = getTriggerKey(scheduleJob);

            CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(scheduleJob.getCronExpression())
                    .withMisfireHandlingInstructionDoNothing();

            CronTrigger cronTrigger = getCronTrigger(scheduler, scheduleJob);

            cronTrigger = cronTrigger.getTriggerBuilder()
                    .withIdentity(triggerKey)
                    .withDescription(scheduleJob.getDescription())
                    .withSchedule(cronScheduleBuilder).build();

            scheduler.rescheduleJob(triggerKey, cronTrigger);

            _logger.info("Update schedule job {}-{} success", scheduleJob.getJobGroup(), scheduleJob.getJobName());

            if (scheduleJob.getPause()) {
                pauseJob(scheduler, scheduleJob);
            }
        } catch (SchedulerException e) {
            _logger.error("Update schedule job failed"+e.getMessage());
        }
    }

    /**
     * 执行任务
     * @param scheduler
     * @param scheduleJob
     */
    public static void run(Scheduler scheduler, ScheduleJob scheduleJob) {
        try {
            scheduler.triggerJob(getJobKey(scheduleJob));
            _logger.info("Run schedule job {}-{} success", scheduleJob.getJobGroup(), scheduleJob.getJobName());
        } catch (SchedulerException e) {
            _logger.error("Run schedule job failed"+e.getMessage());
        }
    }

    /**
     * 暂停任务
     *
     * @param scheduler
     * @param scheduleJob
     */
    public static void pauseJob(Scheduler scheduler, ScheduleJob scheduleJob)  {
        try {
            scheduler.pauseJob(getJobKey(scheduleJob));
            _logger.info("Pause schedule job {}-{} success", scheduleJob.getJobGroup(), scheduleJob.getJobName());
        } catch (SchedulerException e) {
            _logger.error("Pause schedule job failed"+e.getMessage());
        }
    }

    /**
     * 继续执行任务
     *
     * @param scheduler
     * @param scheduleJob
     */
    public static void resumeJob(Scheduler scheduler, ScheduleJob scheduleJob)  {
        try {
            scheduler.resumeJob(getJobKey(scheduleJob));
            _logger.info("Resume schedule job {}-{} success", scheduleJob.getJobGroup(), scheduleJob.getJobName());
        } catch (SchedulerException e) {
            _logger.error("Resume schedule job failed"+e.getMessage());
        }
    }

    /**
     * 删除任务
     *
     * @param scheduler
     * @param scheduleJob
     */
    public static void deleteJob(Scheduler scheduler, ScheduleJob scheduleJob)  {
        try {
            scheduler.deleteJob(getJobKey(scheduleJob));
            _logger.info("Delete schedule job {}-{} success", scheduleJob.getJobGroup(), scheduleJob.getJobName());
        } catch (SchedulerException e) {
            _logger.error("Delete schedule job failed"+e.getMessage());
        }
    }
    /**
     * 校验Cron表达式
     */
    public static void validateCronExpression(ScheduleJob scheduleJob)  {
        if (!CronExpression.isValidExpression(scheduleJob.getCronExpression())) {
            _logger.error("Job %s expression %s is not correct!", scheduleJob.getClassName(), scheduleJob.getCronExpression());
        }
    }
}


