package com.hgicreate.rno.ltestrucanlsclient.client;

import com.hgicreate.rno.ltestrucanlsclient.model.*;
import org.springframework.util.MultiValueMap;

import java.util.List;

public interface RnoLteStrucAnlsRestClient {

    /**
     * 获取LTE结构分析任务的数量
     */
    Long queryLteStrucAnlsTaskCnt(MultiValueMap<String, Object> param);

    /**
     * 分页获取LTE结构分析任务的信息
     */
    List<TaskQueryResult> queryLteStrucAnlsTaskByPage(MultiValueMap<String, Object> param);

    /**
     * 提交LTE 结构优化分析计算任务
     */
    Boolean addLteStrucAnlsTask(SubmitTaskCond cond);

    /**
     * 根据任务ID查询结构优化分析任务的信息
     */
    StrucTaskRecord queryLteStrucAnlsTaskRecByJobId(long jobId);

    /**
     * 根据任务ID获取重叠覆盖结果
     */
    List<OverlapCover> queryOverlapCoverResultByJobId(long jobId);

    /**
     * 根据任务ID获取过覆盖结果
     */
    List<OverCover> queryOverCoverResultByJobId(long jobId);

    /**
     * 根据任务ID获取指标汇总结果
     */
    List<MetricsSummary> queryMetricsSummaryResultByJobId(long jobId);
}
