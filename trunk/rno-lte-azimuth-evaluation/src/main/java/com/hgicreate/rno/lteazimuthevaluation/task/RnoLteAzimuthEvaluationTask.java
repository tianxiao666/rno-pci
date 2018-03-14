package com.hgicreate.rno.lteazimuthevaluation.task;

import com.hgicreate.rno.lteazimuthevaluation.model.*;
import com.hgicreate.rno.lteazimuthevaluation.properties.RnoProperties;
import com.hgicreate.rno.lteazimuthevaluation.service.RnoCommonService;
import com.hgicreate.rno.lteazimuthevaluation.service.RnoLteAzimuthEvaluationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class RnoLteAzimuthEvaluationTask {
    private static final Logger logger = LoggerFactory.getLogger(RnoLteAzimuthEvaluationTask.class);

    private RnoProperties rnoProperties;

    private RnoCommonService rnoCommonService;

    private RnoLteAzimuthEvaluationService rnoLteAzimuthEvaluationService;

    public RnoLteAzimuthEvaluationTask(RnoProperties rnoProperties, RnoCommonService rnoCommonService, RnoLteAzimuthEvaluationService rnoLteAzimuthEvaluationService) {
        this.rnoProperties = rnoProperties;
        this.rnoCommonService = rnoCommonService;
        this.rnoLteAzimuthEvaluationService = rnoLteAzimuthEvaluationService;
    }

    public boolean runJobInternal(long jobId) {

        // 任务状态检查点
        if (isJobStopped(jobId)) {
            logger.debug("LTE天线方位角评估任务被停止");
            return false;
        }

        JobProfile job = rnoLteAzimuthEvaluationService.getJobProfileByJobId(jobId);
        // 开始任务，更新任务状态
        startJob(job, jobId);

        String msg;
        Date startTime = new Date();
        updateOwnProgress(jobId, "开始分析");

        // 通过 jobId 获取Lte方位角计算记录信息(rno_ms_lte_azimuth_asses_job表），包括变小区的 CLOB 信息
        List<AzimuthJobRecord> records = rnoLteAzimuthEvaluationService.queryLteAzimuthEvaluationJobRecByJobId(jobId);

        logger.debug("records:{}", records);

        if (records == null || records.isEmpty()) {
            msg = "不存在此LTE方位角评估分析需要的数据！";
            logger.debug(msg);
            addOneReport(jobId, startTime, msg, TaskStatus.Fail.toString(), "获取任务配置信息");
            updateOwnProgress(jobId, "计算失败");
            endJob(job, JobStatus.Fail.toString());
            return false;
        }

        // 任务状态检查点
        if (isJobStopped(jobId)) {
            logger.debug("LTE天线方位角评估任务被停止");
            return false;
        }

        AzimuthJobRecord record = records.get(0);
        logger.debug("LTE方位角评估分析的数据信息：" + record);

        boolean hasData = rnoLteAzimuthEvaluationService.hasData(record.getEvalType(), record.getCityId(), record.getBegMeaTime(), record.getEndMeaTime());

        // 任务状态检查点
        if (isJobStopped(jobId)) {
            logger.debug("LTE天线方位角评估任务被停止");
            return false;
        }

        if (hasData) {
            startTime = new Date();
            // 开始分析
            ResultInfo resultInfo = new ResultInfo();
            try {
                resultInfo = doLteAzimuthEvaluation(record);
            } catch (Exception e) {
                e.printStackTrace();
                resultInfo.setFlag(false);
            }

            // 任务状态检查点
            if (isJobStopped(jobId)) {
                logger.debug("LTE天线方位角评估任务被停止");
                return false;
            }

            logger.debug("LTE天线方位角评估分析任务完成，result=" + resultInfo);
            // 报告
            if (resultInfo.isFlag()) {
                msg = "LTE天线方位角评估分析完成！";
                addOneReport(jobId, startTime, msg, TaskStatus.Succeeded.toString(), "任务总结");
                updateOwnProgress(jobId, "计算成功");
                endJob(job, JobStatus.Succeeded.toString());
                return true;
            } else {
                logger.error(jobId + "的LTE天线方位角评估分析出错！" + resultInfo.getMsg());
                // 任务状态
                msg = "LTE天线方位角评估分析异常！" + resultInfo.getMsg();
                addOneReport(jobId, startTime, msg, TaskStatus.Fail.toString(), "任务总结");
                updateOwnProgress(jobId, "计算失败");
                endJob(job, JobStatus.Fail.toString());
                return false;
            }
        } else {
            msg = "LTE天线方位角评估分析异常！没有数据！";
            logger.error(jobId + "的LTE天线方位角评估分析出错！没有数据！");
            addOneReport(jobId, startTime, msg, TaskStatus.Fail.toString(), "任务总结");
            updateOwnProgress(jobId, "计算失败");
            endJob(job, JobStatus.Fail.toString());
            return false;
        }
    }

    /**
     * LTE方位角评估分析入口
     */
    private ResultInfo doLteAzimuthEvaluation(AzimuthJobRecord record) {
        logger.debug("汇总MRO>>>>>>>>>>>>>>>开始计算方位角评估...");

        long t1 = System.currentTimeMillis();

        // spark 计算
        List<AzimuthResult> results = rnoLteAzimuthEvaluationService.calcAzimuth(record.getCityId(), record.getBegMeaTime(), record.getEndMeaTime());

        // oracle 入库
        rnoLteAzimuthEvaluationService.batchInsertAzimuthResult(record.getJobId(), results, rnoProperties.getBatch());

        ResultInfo result = new ResultInfo();
        result.setFlag(true);

        logger.debug("汇总MRO完成计算LTE方位角评估，结果:{},耗时:{}", true, (System.currentTimeMillis() - t1));
        return result;
    }

    private void updateOwnProgress(long jobId, String jobStatus) {
        // 更新pci规划干扰计算表的进度
        rnoLteAzimuthEvaluationService.updateLteAzimuthEvaluationStatus(jobId, jobStatus);
    }

    /**
     * 添加报告到数据库
     */
    private void addOneReport(long jobId, Date date1, String msg, String status, String stage) {
        Report report = new Report();
        report.setJobId(jobId);
        report.setBegTime(date1);
        report.setEndTime(new Date());
        report.setFinishState(status);
        report.setStage(stage);
        report.setAttMsg(msg);
        rnoCommonService.addOneReport(report);
    }

    /**
     * 更新任务状态
     */
    private void startJob(JobProfile job, long jobId) {
        // 更行任务运行状态
        job.setJobId(jobId);
        job.setJobStateStr(JobStatus.Running.toString());
        rnoCommonService.updateJobRunningStatus(job);

        // 更新任务开始时间
        job.setLaunchTime(new Date());
        rnoCommonService.updateJobBegTime(job);
    }

    /**
     * 更新任务状态
     */
    private void endJob(JobProfile job, String jobStatus) {
        job.setJobStateStr(jobStatus);
        job.setFinishTime(new Date());
        rnoCommonService.updateJobEndTime(job);
    }

    /**
     * 查看任务状态
     */
    private boolean isJobStopped(long jobId) {
        JobStatus jobStatus = rnoCommonService.checkJobStatus(jobId);
        return JobStatus.Stopped.equals(jobStatus);
    }
}
