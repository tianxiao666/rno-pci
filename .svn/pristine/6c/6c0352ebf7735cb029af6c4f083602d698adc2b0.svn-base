package com.hgicreate.rno.lteazimuthevaluation.dao;

import com.hgicreate.rno.lteazimuthevaluation.model.*;

import java.util.List;
import java.util.Map;

public interface RnoLteAzimuthEvaluationDao {
    /**
     * 添加一条任务信息
     */
    Long addOneJob(JobProfile job);

    /**
     * 保存Lte方位角工作信息
     */
    Long addLteAzimuthEvaluationJob(AzimuthJobRecord cond);

    /**
     * 获取LTE方位角评估分析任务的总数
     */
    Long getLteAzimuthEvaluationJobCount(TaskQueryCond cond);

    /**
     * 通过 jobId 获取Lte方位角计算记录信息
     */
    List<AzimuthJobRecord> queryLteAzimuthEvaluationJobRecByJobId(long jobId);

    /**
     * 获取相应状态下的任务ID
     */
    Long getOneJob(JobProfile job);

    /**
     * 根据任务ID获取任务名称
     */
    JobProfile getJobProfileByJobId(long jobId);

    /**
     * 更新PCI规划的job状态
     */
    Long updateLteAzimuthEvaluationStatus(long jobId, String jobStatus);

    /**
     * 分页获取LTE方位角评估分析任务的信息
     */
    List<TaskQueryResult> queryLteAzimuthEvaluationTaskByPage(TaskQueryCond cond);

    Long batchInsertAzimuthResult(long jobId, List<AzimuthResult> results,int batch);

    List<AzimuthResult> queryAzimuthResultsByJobId(long jobId);
}
