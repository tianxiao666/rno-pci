package com.hgicreate.rno.ltestrucanlsservice.dao;

import com.hgicreate.rno.ltestrucanlsservice.model.MetricsSummary;
import com.hgicreate.rno.ltestrucanlsservice.model.OverCover;
import com.hgicreate.rno.ltestrucanlsservice.model.OverlapCover;

import java.util.List;

public interface RnoLteStrucAnlsSparkDao {
    /**
     * 获取MR数据统计
     */
    Long queryLteMrDataCnt(long cityId, String begTime, String endTime);

    /**
     * 获取结构数据统计
     */
    Long queryLteStructureDataCnt(long cityId, String begTime, String endTime);

    /**
     * 计算重叠覆盖
     */
    List<OverlapCover> calcOverlapCover(long cityId, String begTime, String endTime);

    /**
     * 计算过覆盖
     */
    List<OverCover> calcOverCover(long cityId, String begTime, String endTime);

    /**
     * 处理过覆盖
     */
    Long handleOverCover(long jobId, long cityId, String begTime, String endTime);

    /**
     * 通过任务ID查找任务结果
     */
    List<OverCover> queryOverCoverResultByJobId(long jobId);

    /**
     * 计算指标汇总
     */
    List<MetricsSummary> calcMetricsSummary(long cityId, String begTime, String endTime);
}
