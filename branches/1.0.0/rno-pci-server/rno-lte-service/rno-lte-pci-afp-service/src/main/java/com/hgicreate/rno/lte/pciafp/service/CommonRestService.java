package com.hgicreate.rno.lte.pciafp.service;

import com.hgicreate.rno.lte.pciafp.model.Job;

import java.util.Date;

public interface CommonRestService {
    /**
     * 添加报告到数据库
     */
    void addOneReport(long jobId, Date date1, String msg, String status, String stage);

    /**
     * 更新任务状态
     */
    void startJob(long jobId);

    /**
     * 更新任务状态
     */
    void startJob(Job job);

    /**
     * 更新任务状态
     */
    void endJob(long jobId, String jobStatus);

    /**
     * 更新任务状态
     */
    void endJob(Job job, String jobStatus);

    /**
     * 查看任务状态
     */
    boolean isJobStopped(long jobId);

    Job getOneJob(Job job);

    Job getJobByJobId(long jobId);
}
