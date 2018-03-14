package com.hgicreate.rno.lteazimuthevaluation.service;

import com.hgicreate.rno.lteazimuthevaluation.model.*;

import java.util.List;

public interface RnoLteAzimuthEvaluationService {

    /**
     * 分页查询LTE方位角评估分析任务信息
     */
    List<TaskQueryResult> queryLteAzimuthEvaluationTaskByPage(TaskQueryCond cond, Page page);

    /**
     * 根据任务ID获取任务名称
     */
    JobProfile getJobProfileByJobId(long jobId);

    /**
     * 获取相应状态下的任务ID
     */
    Long getOneJob(JobProfile job);

    /**
     * 更新PCI规划的job状态
     */
    Long updateLteAzimuthEvaluationStatus(long jobId, String jobStatus);

    /**
     * 提交LTE 方位角评估分析计算任务
     */
    SubmitResult submitLteAzimuthEvaluationTask(String account, AzimuthJobRecord taskInfo);

    /**
     * 通过 jobId 获取Lte方位角计算记录信息
     */
    List<AzimuthJobRecord> queryLteAzimuthEvaluationJobRecByJobId(long jobId);

    List<AzimuthResult> calcAzimuth(long cityId, String begTime, String endTime);

    Long batchInsertAzimuthResult(long jobId, List<AzimuthResult> results, int batch);

    List<AzimuthResult> queryAzimuthResultsByJobId(long jobId);

    boolean hasData(String evalType, long cityId, String begTime, String endTime);
}
