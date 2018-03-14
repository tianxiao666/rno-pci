package com.hgicreate.rno.lte.pciafp.client;

import com.hgicreate.rno.lte.pciafp.model.Job;
import com.hgicreate.rno.lte.pciafp.model.Report;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author chen.c10
 */
@Slf4j
@Component
public class CommonRestClientFallback implements CommonRestClient {

    @Override
    public Job saveJob(Job job) {
        log.debug("保存任务失败。");
        return null;
    }

    @Override
    public Job getOneJob(Job job) {
        log.debug("获取任务失败。");
        return null;
    }

    @Override
    public Job getJobByJobId(Long jobId) {
        log.debug("通过ID获取任务失败");
        return null;
    }

    @Override
    public Report saveReport(Report report) {
        log.debug("保存任务报告失败。");
        return null;
    }
}
