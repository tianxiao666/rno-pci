package com.hgicreate.rno.lte.pciafp.client;

import com.hgicreate.rno.lte.pciafp.model.PciAfpParam;
import com.hgicreate.rno.lte.pciafp.model.PciAfpTask;
import com.hgicreate.rno.lte.pciafp.model.PlanItem;
import com.hgicreate.rno.lte.pciafp.model.Threshold;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author chen.c10
 */
@FeignClient(name = "${rno.lte-common-service:rno-lte-common-service}", fallback = PciAfpTaskRestClientFallback.class, path = "/pciAfp")
public interface PciAfpTaskRestClient {
    /**
     * 通过 jobId 获取Lte方位角计算记录信息
     *
     * @param jobId job id
     * @return pci 任务
     */
    @GetMapping("/queryTaskRecord/{jobId}")
    PciAfpTask queryTaskRecordByJobId(@PathVariable("jobId") long jobId);

    /**
     * 更新task状态
     *
     * @param jobId     job id
     * @param jobStatus 任务状态
     * @return 更新是否成功
     */
    @PostMapping("/updateTaskStatus/{jobId}")
    Boolean updateTaskStatus(@PathVariable("jobId") long jobId, @RequestParam("jobStatus") String jobStatus);

    /**
     * 通过模块类型获取阈值门限对象集合
     *
     * @param moduleType 模块类型
     * @return 阈值列表
     */
    @GetMapping("/getThresholds/{moduleType}")
    List<Threshold> getThresholdsByModuleType(@PathVariable("moduleType") String moduleType);

    /**
     * 通过jobId获取页面自定义的阈值门限值
     *
     * @param jobId job id
     * @return pci 任务参数列表
     */
    @GetMapping("/queryParamInfo/{jobId}")
    List<PciAfpParam> queryParamInfo(@PathVariable("jobId") long jobId);

    /**
     * 保存最佳方案
     *
     * @param items 方案条目
     * @return 保存的条数
     */
    @PostMapping("/saveBestPlan")
    Long saveBestPlan(@RequestBody List<PlanItem> items);

    /**
     * 获取最佳方案
     *
     * @param jobId job id
     * @return 方案列表
     */
    @GetMapping("/queryBestPlan/{jobId}")
    List<PlanItem> queryBestPlanByJobId(@PathVariable("jobId") long jobId);
}
