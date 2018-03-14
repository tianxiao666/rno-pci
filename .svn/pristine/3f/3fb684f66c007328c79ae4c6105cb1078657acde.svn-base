package com.hgicreate.rno.ltestrucanlsservice.service;

import com.hgicreate.rno.ltestrucanlsservice.model.*;

import java.util.List;

public interface RnoLteStrucAnlsService {

    /**
     * 获取LTE结构分析任务的总数
     */
    Long queryLteStrucAnlsTaskCnt(TaskQueryCond cond);

    /**
     * 分页获取LTE结构分析任务的信息
     */
    List<TaskQueryResult> queryLteStrucAnlsTaskByPage(TaskQueryCond cond);

    /**
     * 提交LTE 结构优化分析计算任务
     */
    Boolean addLteStrucAnlsTask(StrucTaskRecord task);

    /**
     * 通过 jobId 获取lte结构优化记录信息
     */
    StrucTaskRecord queryLteStrucAnlsTaskRecByJobId(long jobId);

    /**
     * 更新结构优化分析任务状态
     */
    Long updateLteStrucAnlsTaskStatus(long jobId, String jobStatus);

    /**
     * 停止LTE结构优化分析任务
     */
    Long stopLteStrucAnlsTaskById(Long jobId);

    /**
     * 检查是否有数据
     */
    boolean hasData(long cityId, String begTime, String endTime);

    /**
     * 计算重叠覆盖
     */
    List<OverlapCover> calcOverlapCover(long cityId, String begTime, String endTime);

    /**
     * 保存重叠覆盖结果
     */
    Long saveOverlapCover(long jobId, List<OverlapCover> overlapCovers, int batch);

    /**
     * 通过任务ID查找任务结果
     */
    List<OverlapCover> queryOverlapCoverResultByJobId(long jobId);

    /**
     * 计算过覆盖
     */
    List<OverCover> calcOverCover(long cityId, String begTime, String endTime);

    /**
     * 保存过覆盖结果
     */
    Long saveOverCover(long jobId, List<OverCover> overCovers, int batch);

    /**
     * 通过任务ID查找任务结果
     */
    List<OverCover> queryOverCoverResultByJobId(long jobId);

    /**
     * 处理过覆盖
     */
    Long handleOverCover(long jobId, long cityId, String begTime, String endTime);

    /**
     * 计算指标汇总
     */
    List<MetricsSummary> calcMetricsSummary(long cityId, String begTime, String endTime);

    /**
     * 保存指标汇总结果
     */
    Long saveMetricsSummary(long jobId, List<MetricsSummary> metricsSummaries, int batch);

    /**
     * 通过任务ID查找任务结果
     */
    List<MetricsSummary> queryMetricsSummaryResultByJobId(long jobId);
}
