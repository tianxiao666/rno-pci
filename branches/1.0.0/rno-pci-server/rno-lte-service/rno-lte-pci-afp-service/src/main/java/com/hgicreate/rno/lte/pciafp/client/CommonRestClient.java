package com.hgicreate.rno.lte.pciafp.client;

import com.hgicreate.rno.lte.pciafp.model.Job;
import com.hgicreate.rno.lte.pciafp.model.Report;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * @author chen.c10
 */
@FeignClient(name = "${rno.lte-common-service:rno-lte-common-service}", fallback = CommonRestClientFallback.class)
public interface CommonRestClient {
    /**
     * 插入或更新任务
     */
    @RequestMapping(value = "saveJob", method = POST)
    Job saveJob(@RequestBody Job job);

    /**
     * 获取相应状态下的任务ID
     */
    @RequestMapping(value = "getOneJob", method = POST)
    Job getOneJob(@RequestBody Job job);

    /**
     * 根据任务ID获取任务名称
     */
    @RequestMapping(value = "jobs/{jobId}", method = GET)
    Job getJobByJobId(@PathVariable("jobId") Long jobId);

    /**
     * 增加一条新的报告记录
     */
    @RequestMapping(value = "saveReport", method = POST)
    Report saveReport(@RequestBody Report report);
}
