package com.hgicreate.rno.lte.pciafp.service;

import com.hgicreate.rno.lte.pciafp.model.*;

import java.util.List;

public interface PciAfpRestService {

    /**
     * 更新任务自身状态
     */
    boolean updateOwnProgress(long jobId, String jobStatus);

    /**
     * 通过 jobId 获取Lte方位角计算记录信息
     */
    PciAfpTask queryTaskRecordByJobId(long jobId);

    /**
     * 通过模块类型获取阈值门限对象集合
     */
    List<Threshold> getThresholdsByModuleType(String moduleType);

    /**
     * 通过jobId获取页面自定义的阈值门限值
     */
    List<PciAfpParam> queryParamInfo(long jobId);

    /**
     * 保存最佳方案
     */
    Long batchInsertBestPlan(List<PlanItem> items);

    /**
     * 获取最佳方案
     */
    List<PlanItem> queryBestPlanByJobId(long jobId);
}
