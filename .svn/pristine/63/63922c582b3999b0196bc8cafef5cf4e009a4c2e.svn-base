package com.hgicreate.rno.lteazimuthevaluation.schedule;

import com.hgicreate.rno.lteazimuthevaluation.model.JobStatus;
import com.hgicreate.rno.lteazimuthevaluation.model.JobProfile;
import com.hgicreate.rno.lteazimuthevaluation.properties.RnoProperties;
import com.hgicreate.rno.lteazimuthevaluation.service.RnoLteAzimuthEvaluationService;
import com.hgicreate.rno.lteazimuthevaluation.task.RnoLteAzimuthEvaluationTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);

    private RnoProperties rnoProperties;

    private RnoLteAzimuthEvaluationTask rnoLteAzimuthEvaluationTask;

    private RnoLteAzimuthEvaluationService rnoLteAzimuthEvaluationService;

    public ScheduledTasks(RnoProperties rnoProperties, RnoLteAzimuthEvaluationTask rnoLteAzimuthEvaluationTask, RnoLteAzimuthEvaluationService rnoLteAzimuthEvaluationService) {
        this.rnoProperties = rnoProperties;
        this.rnoLteAzimuthEvaluationTask = rnoLteAzimuthEvaluationTask;
        this.rnoLteAzimuthEvaluationService = rnoLteAzimuthEvaluationService;
    }

    @Scheduled(fixedDelayString = "${rno.scheduler.fixed-delay}")
    public void runParseTask() {
        String runMode = rnoProperties.getRunMode();
        if (runMode.equals("scheduler")) {
            logger.debug("定时器检查是否可领取新的Lte方位角计算任务。");
            try {
                JobProfile job = new JobProfile();
                job.setJobStateStr(JobStatus.Waiting.toString());
                job.setJobType("RNO_LTE_AZIMUTH_EVALUATION");

                Long jobId = rnoLteAzimuthEvaluationService.getOneJob(job);

                // 从数据库中取得将运行的数据解析导入任务
                if (jobId != null && jobId > 0) {
                    logger.debug("定时器领取到新的Lte方位角计算任务。");
                    rnoLteAzimuthEvaluationTask.runJobInternal(jobId);
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("定时器检查并运行定时失败！");
            }
        }
    }
}
