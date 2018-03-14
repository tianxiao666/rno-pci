package com.hgicreate.rno.schedule;

import com.hgicreate.rno.model.Job;
import com.hgicreate.rno.model.JobStatus;
import com.hgicreate.rno.service.AzimuthEvalService;
import com.hgicreate.rno.task.JobExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 定时任务
 *
 * @author chen.c10
 * @date 2018-01-12 14:20:20
 */
@Slf4j
@Component
public class ScheduledTasks {
    private static final String RUN_MODE_SCHEDULER = "scheduler";

    private final JobExecutor jobExecutor;

    private final AzimuthEvalService azimuthEvalService;

    @Value("${rno.run-mode:always}")
    private String runMode;
    @Value("${rno.job-type-code:RNO_LTE_AZIMUTH_EVALUATION}")
    private String jobTypeCode;

    public ScheduledTasks(JobExecutor jobExecutor, AzimuthEvalService azimuthEvalService) {
        this.jobExecutor = jobExecutor;
        this.azimuthEvalService = azimuthEvalService;
    }

    @Scheduled(fixedDelayString = "${rno.scheduler.fixed-delay}")
    public void runParseTask() {
        if (Objects.equals(RUN_MODE_SCHEDULER, runMode)) {
            log.debug("定时器检查是否可领取新的Lte方位角计算任务。");
            try {
                Job job = new Job();
                job.setJobRunningStatus(JobStatus.Waiting.toString());
                job.setJobType(jobTypeCode);

                job = azimuthEvalService.getOneJob(job);

                // 从数据库中取得将运行的数据解析导入任务
                if (job != null && job.getJobId() > 0) {
                    log.debug("定时器领取到新的Lte方位角计算任务。");
                    jobExecutor.runJobInternal(job.getJobId());
                }
            } catch (Exception e) {
                e.printStackTrace();
                log.error("定时器检查并运行定时失败！");
            }
        }
    }
}
