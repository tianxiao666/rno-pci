package com.hgicreate.rno.lte.common.controller.azimutheval;

import com.hgicreate.rno.lte.common.model.SubmitResult;
import com.hgicreate.rno.lte.common.model.TaskQueryCond;
import com.hgicreate.rno.lte.common.model.TaskQueryResultDTO;
import com.hgicreate.rno.lte.common.model.azimutheval.*;
import com.hgicreate.rno.lte.common.service.azimutheval.AzimuthEvalResultService;
import com.hgicreate.rno.lte.common.service.azimutheval.AzimuthEvalTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * @author chen.c10
 */
@Slf4j
@RestController
@RequestMapping("/azimuthEval")
public class AzimuthEvalTaskController {
    private final AzimuthEvalTaskService azimuthEvalTaskService;
    private final AzimuthEvalResultService azimuthEvalResultService;

    public AzimuthEvalTaskController(AzimuthEvalTaskService azimuthEvalTaskService,
                                     AzimuthEvalResultService azimuthEvalResultService) {
        this.azimuthEvalTaskService = azimuthEvalTaskService;
        this.azimuthEvalResultService = azimuthEvalResultService;
    }

    /**
     * 查询数据记录
     */
    @PostMapping("/query-task")
    public List<TaskQueryResultDTO> queryTaskByPage(@RequestBody TaskQueryCond cond) {
        log.debug("进入方法:queryTaskByPage。cond = {}", cond);
        List<AzimuthEvalTask> res = azimuthEvalTaskService.findAll(cond);
        log.debug("res={}", res);
        return res.stream().map(TaskQueryResultDTO::new).collect(Collectors.toList());
    }

    /**
     * 通过 jobId 获取Lte方位角计算记录信息
     */
    @GetMapping("/query-task/{jobId}")
    public AzimuthEvalTask queryTaskRecordByJobId(@PathVariable("jobId") long jobId) {
        log.debug("进入方法：queryTaskRecordByJobId。jobId={}", jobId);
        return azimuthEvalTaskService.getOneAzimuthEvalTask(jobId);
    }

    /**
     * 提交LTE 方位角评估分析计算任务
     */
    @PostMapping("/submit-task")
    public SubmitResult submitTask(@RequestBody AzimuthEvaluationSubmitTaskVM cond) {
        log.debug("进入方法:submitTask.cond={}", cond);
        SubmitResult submitResult = azimuthEvalTaskService.submitTask(cond);
        log.debug("离开方法：submitTask。submitResult={}", submitResult);
        return submitResult;
    }

    /**
     * 更新task状态
     */
    @RequestMapping(value = "/updateTaskStatus/{jobId}", method = POST)
    public boolean updateTaskStatus(@PathVariable("jobId") long jobId, String jobStatus) {
        log.debug("进入方法：updateTaskStatus。jobId={},jobStatus={}", jobId, jobStatus);
        return azimuthEvalTaskService.updateTaskStatus(jobId, jobStatus);
    }

    /**
     * 更新task测量时间
     */
    @RequestMapping(value = "/update-meatime/{jobId}/{date}", method = POST)
    public boolean updateTaskMeaTime(@PathVariable("jobId") long jobId, @PathVariable("date") String date) {
        log.debug("进入方法：updateTaskMeaTime。jobId={}, date={}", jobId, date);
        return azimuthEvalTaskService.updateTaskMeaTime(jobId, date);
    }

    /**
     * 根据任务ID获取计算结果
     */
    @Cacheable("azimuthEvalResult")
    @GetMapping("/query-result/{jobId}")
    public List queryResultByJobId(@PathVariable("jobId") long jobId) {
        log.debug("进入方法:queryResultByJobId.jobId={}", jobId);
        return azimuthEvalResultService.findAzimuthEvalResultsByJobId(jobId);
    }

    /**
     * 根据任务ID获取1000计算结果
     */
    @Cacheable("azimuthEvalTop1000Result")
    @GetMapping("/query-1000-result/{jobId}")
    public List queryTop1000ResultByJobId(@PathVariable("jobId") long jobId) {
        log.debug("进入方法:queryTop1000ResultByJobId.jobId={}", jobId);
        return azimuthEvalResultService.findTop1000AzimuthEvalResultsByJobId(jobId);
    }

    /**
     * 根据任务ID获取网络覆盖增强方案结果
     */
    @Cacheable("azimuthEvalEnhancePlan")
    @GetMapping("/query-enhance/{jobId}")
    public List queryEnhanceByJobId(@PathVariable("jobId") long jobId) {
        log.debug("进入方法:queryEnhanceByJobId.jobId={}", jobId);
        List<Map<String, Object>> res = azimuthEvalResultService.findAzimuthEvalResultsByJobId(jobId);
        res.forEach(map -> map.put("result", new Random().nextInt(360) -180));
        log.debug("res={}", res);
        return res;
    }

    /**
     * 根据任务ID获取1000网络覆盖增强方案结果
     */
    @Cacheable("azimuthEvalTop1000EnhancePlan")
    @GetMapping("/query-1000-enhance/{jobId}")
    public List queryTop1000EnhanceByJobId(@PathVariable("jobId") long jobId) {
        log.debug("进入方法:queryTop1000EnhanceByJobId.jobId={}", jobId);
        List<Map<String, Object>> res = azimuthEvalResultService.findTop1000AzimuthEvalResultsByJobId(jobId);
        res.forEach(map -> map.put("result", new Random().nextInt(360) -180));
        return res;
    }

    /**
     * 保存天线方位角结果
     */
    @PostMapping("/saveResult")
    public long saveResult(@RequestBody List<AzimuthEvalResult> azimuthEvalResults) {
        log.debug("进入方法:saveResult.size={}", azimuthEvalResults.size());
        List<AzimuthEvalResult> results = azimuthEvalResultService.saveAzimuthEvalResults(azimuthEvalResults);
        log.debug("退出方法:saveResult.size={}", results.size());
        return results.size();
    }

}
