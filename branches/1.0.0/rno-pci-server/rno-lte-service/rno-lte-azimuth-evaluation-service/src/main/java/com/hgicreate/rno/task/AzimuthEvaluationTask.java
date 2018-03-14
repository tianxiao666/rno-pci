package com.hgicreate.rno.task;

import com.hgicreate.rno.model.*;
import com.hgicreate.rno.service.AzimuthEvalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 任务计算实现
 *
 * @author li.tf
 * @date 2018-01-12 14:36:49
 */
@Slf4j
@Component
public class AzimuthEvaluationTask implements JobExecutor {

    private final AzimuthEvalService azimuthEvalService;

    public AzimuthEvaluationTask(AzimuthEvalService azimuthEvalService) {
        this.azimuthEvalService = azimuthEvalService;
    }

    @Override
    public boolean runJobInternal(long jobId) {
        String msg = "天线方位角评估任务开始";
        Timestamp startTime = new Timestamp(System.currentTimeMillis());
        // 通过 jobId 获取Lte方位角计算记录信息(RNO_LTE_AZIMUTH_EVAL_JOB表），包括变小区的 CLOB 信息
        AzimuthEvalTask record = azimuthEvalService.queryTaskRecordByJobId(jobId);

        log.debug("LTE天线方位角评估的任务信息：{}", record);

        if (record == null) {
            msg = "不存在此LTE方位角评估分析需要的数据！";
            log.debug(msg);
            azimuthEvalService.addOneReport(jobId, startTime, msg, TaskStatus.Fail.toString(), "获取任务配置信息");
            azimuthEvalService.updateOwnProgress(jobId, "计算失败");
            azimuthEvalService.endJob(jobId, startTime, JobStatus.Fail.toString());
            return false;
        }

        Job job = record.getJob();

        // 任务状态检查点
        if (azimuthEvalService.isJobStopped(jobId)) {
            log.debug("LTE天线方位角评估任务被停止");
            return false;
        }

        // 开始任务，更新任务状态
        azimuthEvalService.startJob(job);
        azimuthEvalService.addOneReport(jobId, startTime, msg, TaskStatus.Succeeded.toString(), "任务启动");
        azimuthEvalService.updateOwnProgress(jobId, "开始分析");

        // 任务状态检查点
        if (azimuthEvalService.isJobStopped(jobId)) {
            log.debug("LTE天线方位角评估任务被停止");
            return false;
        }

        // 任务状态检查点
        if (azimuthEvalService.isJobStopped(jobId)) {
            log.debug("LTE天线方位角评估任务被停止");
            return false;
        }

        if (null != azimuthEvalService.getNewestTime()) {
            String year = azimuthEvalService.getNewestTime().get("year").toString();
            String month = azimuthEvalService.getNewestTime().get("month").toString();
            String day = azimuthEvalService.getNewestTime().get("day").toString();
            String time = year.substring(0, year.lastIndexOf("."))
                    + "-" + month.substring(0, month.lastIndexOf("."))
                    + "-" + day.substring(0, day.lastIndexOf("."));
            log.debug("time={}", time);
            azimuthEvalService.updateMeatime(jobId, time);

            // 开始分析
            ResultInfo resultInfo = new ResultInfo();
            try {
                resultInfo = doLteAzimuthEvaluation(record, time);
            } catch (Exception e) {
                e.printStackTrace();
                resultInfo.setFlag(false);
                msg = resultInfo.getMsg();
                azimuthEvalService.addOneReport(jobId, startTime, msg, TaskStatus.Fail.toString(), "计算过程");
                azimuthEvalService.updateOwnProgress(jobId, "计算失败");
                azimuthEvalService.endJob(jobId, startTime, JobStatus.Fail.toString());
                return false;
            }

            // 任务状态检查点
            if (azimuthEvalService.isJobStopped(jobId)) {
                log.debug("LTE天线方位角评估任务被停止");
                return false;
            }

            log.debug("LTE天线方位角评估分析任务完成，result=" + resultInfo);
            // 报告
            if (resultInfo.isFlag()) {
                msg = "LTE天线方位角评估分析完成！";
                azimuthEvalService.addOneReport(jobId, startTime, msg, TaskStatus.Succeeded.toString(), "任务总结");
                azimuthEvalService.updateOwnProgress(jobId, "计算成功");
                azimuthEvalService.endJob(jobId, startTime, JobStatus.Succeeded.toString());
                return true;
            } else {
                log.error(jobId + "的LTE天线方位角评估分析出错！" + resultInfo.getMsg());
                // 任务状态
                msg = "LTE天线方位角评估分析异常！" + resultInfo.getMsg();
                azimuthEvalService.addOneReport(jobId, startTime, msg, TaskStatus.Fail.toString(), "任务总结");
                azimuthEvalService.updateOwnProgress(jobId, "计算失败");
                azimuthEvalService.endJob(jobId, startTime, JobStatus.Fail.toString());
                return false;
            }
        } else {
            msg = "LTE天线方位角评估分析异常！没有数据！";
            log.error(jobId + "的LTE天线方位角评估分析出错！没有数据！");
            azimuthEvalService.addOneReport(jobId, startTime, msg, TaskStatus.Fail.toString(), "任务总结");
            azimuthEvalService.updateOwnProgress(jobId, "计算失败");
            azimuthEvalService.endJob(jobId, startTime, JobStatus.Fail.toString());
            return false;
        }

    }

    /**
     * LTE方位角评估分析入口
     *
     * @param record 任务信息
     * @param time   测量时间
     * @return 方位角评估结果信息
     * @date 2018-01-12 14:33:48
     */
    private ResultInfo doLteAzimuthEvaluation(AzimuthEvalTask record, String time) {
        log.debug("汇总MRO>>>>>>>>>>>>>>>开始计算方位角评估... jobId={}", record.getJobId());

        long t1 = System.currentTimeMillis();

        List<String> cells = Arrays.stream(record.getCells().split(","))
                .map(String::trim).collect(Collectors.toList());

        Map<String, Object> map = new HashMap<>(5);
        map.put("jobId", record.getJobId());
        map.put("cityId", record.getAreaId());
        map.put("begTime", "'" + time + "'");
        map.put("evalType", record.getEvalType());
        map.put("cells", cells);

        // 计算
        List<AzimuthEvalResult> results = azimuthEvalService.calcAzimuth(map);
        log.debug("方位角评估计算完成，开始入库。size={}", results.size());

        // 入库
        Long cnt = azimuthEvalService.batchInsertResult(results);

        ResultInfo result = new ResultInfo();
        result.setFlag(cnt > 0);
        if (cnt < 0) {
            result.setMsg("计算过程出现一点小问题😟");
        } else {
            result.setMsg("计算完成😊");
        }

        log.debug("汇总MRO完成计算LTE方位角评估，结果:{},耗时:{}", true, (System.currentTimeMillis() - t1));
        return result;
    }
}
