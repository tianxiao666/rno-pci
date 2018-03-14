package com.hgicreate.rno.ltestrucanlsservice.dao;

import com.hgicreate.rno.ltestrucanlsservice.model.*;

import java.util.List;

public interface RnoLteStrucAnlsDao {

    /**
     * 保存LTE结构分析任务
     */
    Long addLteStrucAnlsTask(StrucTaskRecord cond);

    /**
     * 通过 jobId 获取lte结构优化分析记录信息
     */
    StrucTaskRecord queryLteStrucAnlsTaskRecByJobId(long jobId);

    /**
     * 获取LTE结构分析任务的总数
     */
    Long queryLteStrucAnlsTaskCnt(TaskQueryCond cond);

    /**
     * 分页获取LTE结构分析任务的信息
     */
    List<TaskQueryResult> queryLteStrucAnlsTaskByPage(TaskQueryCond cond);

    /**
     * 更新结构优化分析任务状态
     */
    Long updateLteStrucAnlsTaskStatus(long jobId, String jobStatus);

    /**
     * 停止一个任务
     */
    Long stopLteStrucAnlsTaskById(Long jobId);

    /**
     * 保存重叠覆盖结果
     */
    Long saveOverlapCover(long jobId, List<OverlapCover> overlapCovers, int batch);

    /**
     * 通过任务ID查找任务结果
     */
    List<OverlapCover> queryOverlapCoverResultByJobId(long jobId);

    /**
     * 保存过覆盖结果
     */
    Long saveOverCover(long jobId, List<OverCover> overCovers, int batch);

    /**
     * 通过任务ID查找任务结果
     */
    List<OverCover> queryOverCoverResultByJobId(long jobId);

    /**
     * 保存指标汇总结果
     */
    Long saveMetricsSummary(long jobId, List<MetricsSummary> metricsSummaries, int batch);

    /**
     * 通过任务ID查找任务结果
     */
    List<MetricsSummary> queryMetricsSummaryResultByJobId(long jobId);
}
