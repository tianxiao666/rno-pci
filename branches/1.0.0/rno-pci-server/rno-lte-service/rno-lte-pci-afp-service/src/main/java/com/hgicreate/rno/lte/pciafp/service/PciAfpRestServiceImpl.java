package com.hgicreate.rno.lte.pciafp.service;

import com.hgicreate.rno.lte.pciafp.client.PciAfpTaskRestClient;
import com.hgicreate.rno.lte.pciafp.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

/**
 * @author chen.c10
 */
@Slf4j
@Service
public class PciAfpRestServiceImpl implements PciAfpRestService {

    private final PciAfpTaskRestClient pciAfpTaskRestClient;

    @Value("${rno.batch:10000}")
    private int batch;

    public PciAfpRestServiceImpl(PciAfpTaskRestClient pciAfpTaskRestClient) {
        this.pciAfpTaskRestClient = pciAfpTaskRestClient;
    }

    @Override
    public boolean updateOwnProgress(long jobId, String jobStatus) {
        return pciAfpTaskRestClient.updateTaskStatus(jobId, jobStatus);
    }

    @Override
    public PciAfpTask queryTaskRecordByJobId(long jobId) {
        return pciAfpTaskRestClient.queryTaskRecordByJobId(jobId);
    }

    @Override
    public List<Threshold> getThresholdsByModuleType(String moduleType) {
        return pciAfpTaskRestClient.getThresholdsByModuleType(moduleType);
    }

    @Override
    public List<PciAfpParam> queryParamInfo(long jobId) {
        return pciAfpTaskRestClient.queryParamInfo(jobId);
    }

    @Override
    public Long batchInsertBestPlan(List<PlanItem> items) {
        final AtomicLong cnt = new AtomicLong();

        final Map<Integer, List<PlanItem>> group = new HashMap<>(items.size() / batch);
        IntStream.range(0, items.size()).forEach(e -> {
            int gid = e / batch;
            group.computeIfAbsent(gid, k -> new ArrayList<>()).add(items.get(e));
        });

        // 由于是绑定参数的提交方式，第一次耗时比较多，之后可以重用，速度较快。
        group.values().forEach(e -> cnt.addAndGet(pciAfpTaskRestClient.saveBestPlan(e)));

        return cnt.get();
    }

    @Override
    public List<PlanItem> queryBestPlanByJobId(long jobId) {
        return pciAfpTaskRestClient.queryBestPlanByJobId(jobId);
    }
}
