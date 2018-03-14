package com.hgicreate.rno.lte.common.repo;

import com.hgicreate.rno.lte.common.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author chen.c10
 */
public interface JobRepository extends JpaRepository<Job, Long> {
    /**
     * 通过任务运行状态和任务类型并按创建时间降序排列获取一个任务
     * @param jobRunningStatus 任务运行状态
     * @param jobType 任务类型
     * @return 任务对象
     */
    List<Job> findByJobRunningStatusAndJobTypeOrderByCreateTimeDesc(String jobRunningStatus, String jobType);

    /**
     *  通过任务ID获取任务
     * @param jobId 任务ID
     * @return 任务对象
     */
    Job getByJobId(Long jobId);
}
