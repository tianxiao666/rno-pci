package com.hgicreate.rno.lte.pciafp.service;

import com.hgicreate.rno.lte.pciafp.client.CommonRestClient;
import com.hgicreate.rno.lte.pciafp.model.Job;
import com.hgicreate.rno.lte.pciafp.model.JobStatus;
import com.hgicreate.rno.lte.pciafp.model.Report;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
public class CommonRestServiceImpl implements CommonRestService {

    private final CommonRestClient commonRestClient;

    public CommonRestServiceImpl(CommonRestClient commonRestClient) {
        this.commonRestClient = commonRestClient;
    }

    @Override
    public void addOneReport(long jobId, Date date1, String msg, String status, String stage) {
        Report report = new Report();
        report.setJobId(jobId);
        report.setBegTime(date1);
        report.setEndTime(new Date());
        report.setState(status);
        report.setStage(stage);
        report.setAttMsg(msg);
        commonRestClient.saveReport(report);
    }

    @Override
    public void startJob(long jobId) {
        Job job = commonRestClient.getJobByJobId(jobId);
        if (null != job && job.getJobId() > 0) {
            startJob(job);
        }
    }

    @Override
    public void startJob(Job job) {
        // 更行任务运行状态
        job.setJobRunningStatus(JobStatus.Running.toString());
        // 更新任务开始时间
        job.setLaunchTime(new Date());
        commonRestClient.saveJob(job);
    }

    @Override
    public void endJob(long jobId, String jobStatus) {
        Job job = commonRestClient.getJobByJobId(jobId);
        if (null != job && job.getJobId() > 0) {
            endJob(job, jobStatus);
        }
    }

    @Override
    public void endJob(Job job, String jobStatus) {
        job.setJobRunningStatus(jobStatus);
        job.setCompleteTime(new Date());
        commonRestClient.saveJob(job);
    }

    @Override
    public boolean isJobStopped(long jobId) {
        Job job = commonRestClient.getJobByJobId(jobId);
        return !(job == null || job.getJobId() <= 0) && JobStatus.Stopped.equals(JobStatus.valueOf(job.getJobRunningStatus()));
    }

    @Override
    public Job getOneJob(Job job) {
        return commonRestClient.getOneJob(job);
    }

    @Override
    public Job getJobByJobId(long jobId) {
        return commonRestClient.getJobByJobId(jobId);
    }
}
