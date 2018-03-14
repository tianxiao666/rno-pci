package com.hgicreate.rno.repo;

import com.hgicreate.rno.model.Job;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 任务仓库
 *
 * @author li.tf
 * @date 2018-01-12 14:17:23
 */
@CacheConfig(cacheNames = "jobs")
public interface JobRepository extends JpaRepository<Job, Long> {

    @CachePut
    @Override
    <S extends Job> S save(S entity);

    @Cacheable
    @Override
    Job getOne(Long id);

    @Cacheable
    Job getOneByJobId(Long id);

    @Cacheable
    Job findByJobRunningStatusAndJobTypeOrderByCreateTimeDesc(String jobRunningStatus, String jobType);

    @CacheEvict
    @Override
    void delete(Job entity);
}
