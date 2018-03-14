package com.hgicreate.rno.lte.common.controller.pciafp;

import com.hgicreate.rno.lte.common.model.*;
import com.hgicreate.rno.lte.common.model.pciafp.PlanItem;
import com.hgicreate.rno.lte.common.model.pciafp.PciAfpParam;
import com.hgicreate.rno.lte.common.model.pciafp.PciAfpTaskObj;
import com.hgicreate.rno.lte.common.model.pciafp.Threshold;
import com.hgicreate.rno.lte.common.service.pciafp.PciAfpResultService;
import com.hgicreate.rno.lte.common.service.pciafp.PciAfpTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

/**
 * @author chen.c10
 */
@Slf4j
@RestController
@RequestMapping("/pciAfp")
public class PciAfpTaskController {
    private final PciAfpTaskService pciAfpTaskService;
    private final PciAfpResultService pciAfpResultService;

    public PciAfpTaskController(PciAfpTaskService pciAfpTaskService, PciAfpResultService pciAfpResultService) {
        this.pciAfpTaskService = pciAfpTaskService;
        this.pciAfpResultService = pciAfpResultService;
    }

    /**
     * 查询数据记录
     */
    @PostMapping("/queryTaskByPage")
    public PageResultBody queryTaskByPage(@RequestBody PageCondBody<TaskQueryCond> condBody) {
        log.debug("进入方法:queryTaskByPage。condBody = {}", condBody);
        TaskQueryCond cond = condBody.getCond();
        Page page = condBody.getPage();

        Pageable pageable = new PageRequest(page.getCurrentPage() - 1, page.getPageSize(), Sort.Direction.DESC, "createTime");
        org.springframework.data.domain.Page result = pciAfpTaskService.findAll(cond, pageable);

        page.setTotalCnt(result.getTotalElements());
        page.setTotalPageCnt(result.getTotalPages());
        page.setForcedStartIndex(-1);

        PageResultBody resultBody = new PageResultBody();
        resultBody.setResult("success");
        resultBody.setData(result.getContent() == null ? Collections.emptyList() : result.getContent());
        resultBody.setPage(page);
        log.debug("退出方法：queryTaskByPage。resultBody={}", resultBody);
        return resultBody;
    }

    /**
     * 通过 jobId 获取Lte方位角计算记录信息
     */
    @GetMapping("/queryTaskRecord/{jobId}")
    public AbstractTask queryTaskRecordByJobId(@PathVariable("jobId")   Long jobId) {
        log.debug("进入方法：queryTaskRecordByJobId。jobId={}", jobId);
        return pciAfpTaskService.getOne(jobId);
    }

    /**
     * 提交LTE 方位角评估分析计算任务
     */
    @PostMapping("/submitTask")
    public SubmitResult submitTask(@RequestBody PciAfpTaskObj cond) {
        log.debug("进入方法:submitTask.cond={}", cond);
        SubmitResult submitResult = pciAfpTaskService.submitTask(cond);
        log.debug("离开方法：submitTask。submitResult={}", submitResult);
        return submitResult;
    }

    /**
     * 更新task状态
     */
    @PostMapping("/pci-afp-task/update-task-status/{jobId}")
    public boolean updatePciAfpTaskStatus(@PathVariable("jobId") long jobId, String jobStatus) {
        log.debug("进入方法：updatePciAfpTaskStatus。jobId={},jobStatus={}", jobId, jobStatus);
        return pciAfpTaskService.updateTaskStatus(jobId, jobStatus);
    }

    /**
     * 更新task状态
     */
    @PutMapping("/updateTaskStatus/{jobId}")
    public boolean updateTaskStatus(@PathVariable("jobId") long jobId, String jobStatus) {
        log.debug("进入方法：updateTaskStatus。jobId={},jobStatus={}", jobId, jobStatus);
        return pciAfpTaskService.updateTaskStatus(jobId, jobStatus);
    }

    /**
     * 通过模块类型获取阈值门限对象集合
     */
    @GetMapping("/getThresholds/{moduleType}")
    public List<Threshold> getThresholdsByModuleType(@PathVariable("moduleType") String moduleType) {
        log.debug("进入方法:getThresholdsByModuleType.moduleType={}", moduleType);
        return pciAfpTaskService.getThresholdsByModuleType(moduleType);
    }

    /**
     * 通过jobId获取页面自定义的阈值门限值
     */
    @GetMapping("/queryParamInfo/{jobId}")
    public List<PciAfpParam> queryParamInfo(@PathVariable("jobId") long jobId) {
        log.debug("进入方法:queryParamInfo.jobId={}", jobId);
        return pciAfpTaskService.queryParamInfo(jobId);
    }

    /**
     * 保存最佳方案
     */
    @PostMapping("/saveBestPlan")
    public Integer saveBestPlan(@RequestBody List<PlanItem> items) {
        log.debug("进入方法:saveBestPlan.items.size={}", items.size());
        List<PlanItem> result = pciAfpResultService.saveBestPlan(items);
        log.debug("退出方法:saveBestPlan.result.size={}", result.size());
        return result.size();
    }

    /**
     * 获取最佳方案
     */
    @GetMapping("/queryBestPlan/{jobId}")
    public List<PlanItem> queryBestPlanByJobId(@PathVariable("jobId") long jobId) {
        log.debug("进入方法:queryBestPlanByJobId.jobId={}", jobId);
        return pciAfpResultService.queryBestPlanByJobId(jobId);
    }
}
