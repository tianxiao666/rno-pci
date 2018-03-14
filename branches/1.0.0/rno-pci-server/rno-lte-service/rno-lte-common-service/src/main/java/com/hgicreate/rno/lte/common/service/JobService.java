package com.hgicreate.rno.lte.common.service;

import com.hgicreate.rno.lte.common.model.Job;
import com.hgicreate.rno.lte.common.repo.JobRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author chen.c10
 */
@Slf4j
@Service
public class JobService {
    private final JobRepository jobRepository;

    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    public Job saveJob(Job job) {
        return jobRepository.save(job);
    }

    public Job findOne(Long jobId) {
//        return jobRepository.getOne(jobId);
        return jobRepository.getByJobId(jobId);
    }

    public Job getOneJob(Job job) {
        List<Job> jobs = jobRepository.findByJobRunningStatusAndJobTypeOrderByCreateTimeDesc(job.getJobRunningStatus(), job.getJobType());
        return null != jobs && jobs.size() > 0 ? jobs.get(0) : null;
    }

    public void delete(Job job) {
        jobRepository.delete(job);
    }
}
