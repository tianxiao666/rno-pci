package com.hgicreate.rno.ltestrucanlsservice.controller;

import com.hgicreate.rno.ltestrucanlsservice.model.*;
import com.hgicreate.rno.ltestrucanlsservice.properties.RnoProperties;
import com.hgicreate.rno.ltestrucanlsservice.service.RnoLteStrucAnlsService;
import com.hgicreate.rno.ltestrucanlsservice.task.RnoLteStrucAnlsTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
public class RnoLteStrucAnlsServiceController {
    private static final Logger logger = LoggerFactory.getLogger(RnoLteStrucAnlsServiceController.class);
    private RnoProperties rnoProperties;
    private RnoLteStrucAnlsService rnoLteStrucAnlsService;
    private RnoLteStrucAnlsTask rnoLteStrucAnlsTask;

    public RnoLteStrucAnlsServiceController(RnoProperties rnoProperties, RnoLteStrucAnlsService rnoLteStrucAnlsService, RnoLteStrucAnlsTask rnoLteStrucAnlsTask) {
        this.rnoProperties = rnoProperties;
        this.rnoLteStrucAnlsService = rnoLteStrucAnlsService;
        this.rnoLteStrucAnlsTask = rnoLteStrucAnlsTask;
    }

    /**
     * 查询结构优化分析任务的数量
     */
    @PostMapping("/queryLteStrucAnlsTaskCnt")
    public Long queryLteStrucAnlsTaskCnt(TaskQueryCond cond) {
        logger.debug("queryJobReportCnt.cond={}", cond);
        // 指定工作类型
        cond.setJobType("RNO_LTE_STRUC_ANLS");
        return rnoLteStrucAnlsService.queryLteStrucAnlsTaskCnt(cond);
    }

    /**
     * 分页查询结构优化分析任务
     */
    @PostMapping("/queryLteStrucAnlsTaskByPage")
    public List<TaskQueryResult> queryLteStrucAnlsTaskByPage(TaskQueryCond cond) {
        logger.debug("queryReportByPage.cond={}", cond);
        // 指定工作类型
        cond.setJobType("RNO_LTE_STRUC_ANLS");
        return rnoLteStrucAnlsService.queryLteStrucAnlsTaskByPage(cond);
    }

    /**
     * 提交LTE结构优化分析任务
     */
    @PostMapping("/addLteStrucAnlsTask")
    public Boolean addLteStrucAnlsTask(StrucTaskRecord task) {
        logger.debug("addLteStrucAnlsTask， task={}", task);
        Boolean result = rnoLteStrucAnlsService.addLteStrucAnlsTask(task);
        if (result != null && result) {
            // 如果运行模式是定时模式，文件上传成功后就直接结束
            String runMode = rnoProperties.getRunMode();
            if (runMode.equals("always")) {
                logger.debug("运行模式是 always 模式，直接执行任务。jobId={}", task.getJobId());
                new JobThread(task.getJobId(), rnoLteStrucAnlsTask).start();
            }
            return true;
        }
        return result;
    }

    /**
     * 终止LTE结构优化分析任务
     */
    @PutMapping("/stopLteStrucAnlsTask/{jobId}")
    public Long stopLteStrucAnlsTask(Long jobId) {
        logger.debug("进入方法：stopLteStrucAnlsTask。jobId={}", jobId);
        return rnoLteStrucAnlsService.stopLteStrucAnlsTaskById(jobId);
    }

    /**
     * 根据任务ID查询结构优化分析的任务信息
     */
    @GetMapping("/queryLteStrucAnlsTaskRec/{jobId}")
    public StrucTaskRecord queryLteStrucAnlsTaskRec(@PathVariable("jobId") long jobId) {
        logger.debug("queryLteStrucAnlsTaskRec.jobId={}", jobId);
        return rnoLteStrucAnlsService.queryLteStrucAnlsTaskRecByJobId(jobId);
    }

    /**
     * 根据任务ID获取重叠覆盖结果
     */
    @GetMapping("/queryOverlapCoverResult/{jobId}")
    public List<OverlapCover> queryOverlapCoverResult(@PathVariable("jobId") long jobId) {
        logger.debug("queryOverlapCoverResult.jobId={}", jobId);
        return rnoLteStrucAnlsService.queryOverlapCoverResultByJobId(jobId);
    }

    /**
     * 根据任务ID获取重叠覆盖结果
     */
    @GetMapping("/queryOverCoverResult/{jobId}")
    public List<OverCover> queryOverCoverResult(@PathVariable("jobId") long jobId) {
        logger.debug("queryOverCoverResult.jobId={}", jobId);
        return rnoLteStrucAnlsService.queryOverCoverResultByJobId(jobId);
    }

    /**
     * 根据任务ID获取重叠覆盖结果
     */
    @GetMapping("/queryMetricsSummaryResult/{jobId}")
    public List<MetricsSummary> queryMetricsSummaryResult(@PathVariable("jobId") long jobId) {
        logger.debug("queryMetricsSummaryResult.jobId={}", jobId);
        return rnoLteStrucAnlsService.queryMetricsSummaryResultByJobId(jobId);
    }

    private class JobThread extends Thread {
        private long jobId;
        private RnoLteStrucAnlsTask rnoLteStrucAnlsTask;

        private JobThread(long jobId, RnoLteStrucAnlsTask rnoLteStrucAnlsTask) {
            this.jobId = jobId;
            this.rnoLteStrucAnlsTask = rnoLteStrucAnlsTask;
        }

        @Override
        public void run() {
            rnoLteStrucAnlsTask.runJobInternal(jobId);
        }
    }
}
