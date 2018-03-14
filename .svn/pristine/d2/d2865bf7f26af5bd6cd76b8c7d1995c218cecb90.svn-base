package com.hgicreate.rno.lte.pciafp.client;

import com.hgicreate.rno.lte.pciafp.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * @author chen.c10
 */
@Slf4j
@Component
public class PciAfpTaskRestClientFallback implements PciAfpTaskRestClient {
    private static final String FAIL_MSG = "找不到公共服务。";

    @Override
    public PciAfpTask queryTaskRecordByJobId(long jobId) {
        log.debug("获取任务记录失败。");
        return null;
    }

    @Override
    public Boolean updateTaskStatus(long jobId, String jobStatus) {
        log.debug("更新任务状态失败。");
        return false;
    }

    @Override
    public List<Threshold> getThresholdsByModuleType(String moduleType) {
        log.debug("获取阈值失败。");
        return Collections.emptyList();
    }

    @Override
    public List<PciAfpParam> queryParamInfo(long jobId) {
        log.debug("获取参数失败。");
        return Collections.emptyList();
    }

    @Override
    public Long saveBestPlan(List<PlanItem> items) {
        log.debug("保存最佳方案失败。");
        return 0L;
    }

    @Override
    public List<PlanItem> queryBestPlanByJobId(long jobId) {
        log.debug("获取最佳方案失败。");
        return Collections.emptyList();
    }
}
