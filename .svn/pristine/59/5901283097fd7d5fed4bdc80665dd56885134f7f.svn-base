package com.hgicreate.rno.lte.common.controller;

import com.hgicreate.rno.lte.common.model.Job;
import com.hgicreate.rno.lte.common.service.JobService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author chen.c10
 */
@Slf4j
@CrossOrigin
@RestController
public class JobController {
    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    /**
     * 插入或更新任务
     */
    @PostMapping("/saveJob")
    public Job saveJob(@RequestBody Job job) {
        log.debug("进入方法：saveJob。job={}", job);
        return jobService.saveJob(job);
    }

    /**
     * 获取相应状态下的任务ID
     */
    @PostMapping("/getOneJob")
    public Job getOneJob(@RequestBody Job job) {
        log.debug("进入方法：getOneJob。job={}", job);
        Job newJob = jobService.getOneJob(job);
        log.debug("退出方法：getOneJob。job={}", newJob);
        return newJob;
    }

    /**
     * 根据任务ID获取任务名称
     */
    @GetMapping("/jobs/{jobId}")
    public Job getJobByJobId(@PathVariable("jobId") Long jobId) {
        log.info("进入方法：getJobByJobId。jobId={}", jobId);
        Job job = jobService.findOne(jobId);
        log.debug("退出方法：getJobByJobId。job={}", job);
        return job;
    }

    /**
     * 终止任务
     */
    @RequestMapping("/stopJob/{jobId}")
    public Map<String, Object> stopJobByJobId(@PathVariable("jobId") Long jobId) {
        log.debug("进入方法：stopJobByJobId。jobId={}", jobId);

        Map<String, Object> map = new HashMap<>();
        String msg = "任务已停止";
        if (jobId == null || jobId <= 0) {
            msg = "未传入一个有效的jobId！无法停止任务！";
            log.error(msg);
            map.put("flag", false);
            map.put("result", msg);
            return map;
        }
        Job job = jobService.findOne(jobId);
        log.debug("stopJobByJobId。job={}", job);
        if (null != job && job.canStop()) {
            job.stop();
            jobService.saveJob(job);
        }
        map.put("flag", true);
        map.put("result", msg);
        return map;
    }
}
