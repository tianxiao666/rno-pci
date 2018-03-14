package com.hgicreate.rno.ltestrucanlsservice.task;

import com.hgicreate.rno.ltestrucanlsservice.model.*;
import com.hgicreate.rno.ltestrucanlsservice.properties.RnoProperties;
import com.hgicreate.rno.ltestrucanlsservice.service.RnoCommonService;
import com.hgicreate.rno.ltestrucanlsservice.service.RnoLteStrucAnlsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class RnoLteStrucAnlsTask {

    private static final Logger logger = LoggerFactory.getLogger(RnoLteStrucAnlsTask.class);

    private RnoProperties rnoProperties;

    private RnoCommonService rnoCommonService;

    private RnoLteStrucAnlsService rnoLteStrucAnlsService;

    public RnoLteStrucAnlsTask(RnoProperties rnoProperties, RnoCommonService rnoCommonService, RnoLteStrucAnlsService rnoLteStrucAnlsService) {
        this.rnoProperties = rnoProperties;
        this.rnoCommonService = rnoCommonService;
        this.rnoLteStrucAnlsService = rnoLteStrucAnlsService;
    }

    public boolean runJobInternal(long jobId) {

        // 任务状态检查点
        if (isJobStopped(jobId)) {
            logger.debug("LTE结构优化分析任务被停止");
            return false;
        }

        JobProfile job = rnoCommonService.getJobByJobId(jobId);
        // 开始任务，更新任务状态
        startJob(job, jobId);

        String msg;
        Date startTime = new Date();
        updateOwnProgress(jobId, TaskState.Starting.toString());

        StrucTaskRecord record = rnoLteStrucAnlsService.queryLteStrucAnlsTaskRecByJobId(jobId);

        logger.debug("LTE结构优化分析的数据信息：" + record);

        if (record == null) {
            msg = "不存在此LTE结构优化分析需要的数据！";
            logger.debug(msg);
            addOneReport(jobId, startTime, msg, ReportStatus.Fail.toString(), "获取任务配置信息");
            updateOwnProgress(jobId, TaskState.Fail.toString());
            endJob(job, JobStatus.Fail.toString());
            return false;
        }

        // 任务状态检查点
        if (isJobStopped(jobId)) {
            logger.debug("LTE结构优化分析任务被停止");
            return false;
        }

        boolean hasData = rnoLteStrucAnlsService.hasData(record.getCityId(), record.getBegMeaTime(), record.getEndMeaTime());

        // 任务状态检查点
        if (isJobStopped(jobId)) {
            logger.debug("LTE结构优化分析任务被停止");
            return false;
        }

        if (hasData) {
            startTime = new Date();
            // 开始分析
            ResultInfo resultInfo = new ResultInfo(false);
            try {
                resultInfo = doLteStructAnalysis(record);
            } catch (Exception e) {
                e.printStackTrace();
                resultInfo.setFlag(false);
            }

            // 任务状态检查点
            if (isJobStopped(jobId)) {
                logger.debug("LTE结构优化分析任务被停止");
                return false;
            }

            logger.debug("LTE结构优化分析任务完成，result=" + resultInfo);
            // 报告
            if (resultInfo.isFlag()) {
                msg = "LTE结构优化分析完成！";
                addOneReport(jobId, startTime, msg, ReportStatus.Succeeded.toString(), "任务总结");
                updateOwnProgress(jobId, TaskState.Succeeded.toString());
                endJob(job, JobStatus.Succeeded.toString());
                return true;
            } else {
                logger.error(jobId + "的LTE结构优化分析出错！" + resultInfo.getMsg());
                msg = "LTE结构优化分析异常！" + resultInfo.getMsg();
                addOneReport(jobId, startTime, msg, ReportStatus.Fail.toString(), "任务总结");
                updateOwnProgress(jobId, TaskState.Fail.toString());
                endJob(job, JobStatus.Fail.toString());
                return false;
            }
        } else {
            msg = "LTE结构优化分析异常！没有数据！";
            logger.error(jobId + "的LTE结构优化分析出错！没有数据！");
            addOneReport(jobId, startTime, msg, ReportStatus.Fail.toString(), "任务总结");
            updateOwnProgress(jobId, TaskState.Fail.toString());
            endJob(job, JobStatus.Fail.toString());
            return false;
        }
    }

    private ResultInfo doLteStructAnalysis(StrucTaskRecord record) {
        logger.debug("LTE结构优化分析开始");
        ResultInfo result = new ResultInfo(true);

        String stage = "";
        try {
            stage = "计算重叠覆盖数据";
            List<OverlapCover> overlapCovers = calcOverlapCover(record);

            stage = "保存重叠覆盖数据";
            saveOverlapCover(overlapCovers, record);

//            stage = "计算过覆盖";
//            List<OverCover> overCovers = calcOverCover(record);
//
//            stage = "保存过覆盖";
//            saveOverCover(overCovers, record);

            stage = "处理过覆盖";
            handleOverCover(record);

            stage = "计算汇总指标";
            List<MetricsSummary> metricsSummaries = calcMetricsSummary(record);

            stage = "保存汇总指标";
            saveMetricsSummary(metricsSummaries, record);

        } catch (Exception e1) {
            e1.printStackTrace();
            result.setFlag(false);
            result.setMsg(stage + "失败！");
        }
        logger.debug("LTE结构优化分析结束");
        return result;
    }

    /**
     * 计算重叠覆盖
     */
    private List<OverlapCover> calcOverlapCover(StrucTaskRecord record) {
        logger.debug("计算重叠覆盖开始");
        long t1 = System.currentTimeMillis();
        List<OverlapCover> overlapCovers = rnoLteStrucAnlsService.calcOverlapCover(record.getCityId(), record.getBegMeaTime(), record.getEndMeaTime());
        logger.debug("计算重叠覆盖耗时：{}", System.currentTimeMillis() - t1);
        return overlapCovers;
    }

    /**
     * 保存重叠覆盖指标
     */
    private boolean saveOverlapCover(List<OverlapCover> overlapCovers, StrucTaskRecord record) {
        logger.debug("进入方法：saveOverlapCover。overlapCovers.size={},record={}", overlapCovers.size(), record);
        rnoLteStrucAnlsService.saveOverlapCover(record.getJobId(), overlapCovers, rnoProperties.getBatch());
        logger.debug("退出方法：saveOverlapCover。");
        return true;
    }

    /**
     * 计算过覆盖
     */
    private List<OverCover> calcOverCover(StrucTaskRecord record) {
        logger.debug("计算过覆盖开始");
        long t1 = System.currentTimeMillis();
        List<OverCover> overCovers = rnoLteStrucAnlsService.calcOverCover(record.getCityId(), record.getBegMeaTime(), record.getEndMeaTime());
        logger.debug("计算过覆盖耗时：{}", System.currentTimeMillis() - t1);
        return overCovers;
    }

    /**
     * 保存过覆盖指标
     */
    private boolean saveOverCover(List<OverCover> overCovers, StrucTaskRecord record) {
        logger.debug("进入方法：saveOverCover。overCovers.size={},record={}", overCovers.size(), record);
        rnoLteStrucAnlsService.saveOverCover(record.getJobId(), overCovers, rnoProperties.getBatch());
        logger.debug("退出方法：saveOverCover。");
        return true;
    }

    /**
     * 处理过覆盖
     */
    private void handleOverCover(StrucTaskRecord record) {
        logger.debug("处理过覆盖开始");
        long t1 = System.currentTimeMillis();
        rnoLteStrucAnlsService.handleOverCover(record.getJobId(), record.getCityId(), record.getBegMeaTime(), record.getEndMeaTime());
        logger.debug("处理过覆盖耗时：{}", System.currentTimeMillis() - t1);
    }

    /**
     * 计算指标汇总
     */
    private List<MetricsSummary> calcMetricsSummary(StrucTaskRecord record) {
        logger.debug("计算指标汇总开始");
        long t1 = System.currentTimeMillis();
        List<MetricsSummary> metricsSummaries = rnoLteStrucAnlsService.calcMetricsSummary(record.getCityId(), record.getBegMeaTime(), record.getEndMeaTime());
        logger.debug("计算指标汇总耗时：{}", System.currentTimeMillis() - t1);
        return metricsSummaries;
    }

    /**
     * 保存指标汇总
     */
    private boolean saveMetricsSummary(List<MetricsSummary> metricsSummaries, StrucTaskRecord record) {
        logger.debug("进入方法：saveMetricsSummary。overCovers.size={},record={}", metricsSummaries.size(), record);
        rnoLteStrucAnlsService.saveMetricsSummary(record.getJobId(), metricsSummaries, rnoProperties.getBatch());
        logger.debug("退出方法：saveMetricsSummary。");
        return true;
    }

    private void updateOwnProgress(long jobId, String jobStatus) {
        rnoLteStrucAnlsService.updateLteStrucAnlsTaskStatus(jobId, jobStatus);
    }

    /**
     * 添加报告到数据库
     */
    private void addOneReport(long jobId, Date date1, String msg, String status, String stage) {
        Date date2;
        Report report = new Report();
        date2 = new Date();
        report.setJobId(jobId);
        report.setBegTime(date1);
        report.setEndTime(date2);
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

    private boolean isJobStopped(long jobId) {
        JobStatus jobStatus = rnoCommonService.checkJobStatus(jobId);
        return JobStatus.Stopped.equals(jobStatus);
    }
}
