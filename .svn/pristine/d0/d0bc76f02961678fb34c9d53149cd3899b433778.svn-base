package com.hgicreate.rno.ltestrucanlsservice.mapper.oracle;

import com.hgicreate.rno.ltestrucanlsservice.model.*;

import java.util.List;
import java.util.Map;

public interface OracleMapper {

    /**
     * 通过 jobId 获取lte结构优化分析记录信息
     */
    StrucTaskRecord queryLteStrucAnlsTaskRecByJobId(long jobId);

    /**
     * 更新lte结构优化分析的任务状态
     */
    Long updateLteStrucAnlsTaskStatus(Map<String, Object> map);

    /**
     * 获取LTE结构分析任务的总数
     */
    Long queryLteStrucAnlsTaskCnt(TaskQueryCond cond);

    /**
     * 分页获取LTE结构分析任务的信息
     */
    List<TaskQueryResult> queryLteStrucAnlsTaskByPage(TaskQueryCond cond);

    /**
     * 保存LTE结构分析任务
     */
    Long addLteStrucAnlsTask(StrucTaskRecord cond);

    /**
     * 停止一个任务
     */
    Long stopLteStrucAnlsTaskById(Long jobId);

    /**
     * 批量导入重叠覆盖计算结果到数据库
     */
    Long batchInsertOverlapCoverResult(Map<String, Object> map);

    /**
     * 通过任务ID查找任务结果
     */
    List<OverlapCover> queryOverlapCoverResultByJobId(long jobId);

    /**
     * 批量导入过覆盖计算结果到数据库
     */
    Long batchInsertOverCoverResult(Map<String, Object> map);

    /**
     * 通过任务ID查找任务结果
     */
    List<OverCover> queryOverCoverResultByJobId(long jobId);

    /**
     * 批量导入指标汇总计算结果到数据库
     */
    Long batchInsertMetricsSummaryResult(Map<String, Object> map);

    /**
     * 通过任务ID查找任务结果
     */
    List<MetricsSummary> queryMetricsSummaryResultByJobId(long jobId);
}
