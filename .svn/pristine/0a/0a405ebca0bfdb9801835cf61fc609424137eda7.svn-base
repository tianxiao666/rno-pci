package com.hgicreate.rno.repo;

import com.hgicreate.rno.model.Report;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 报告仓库
 *
 * @author li.tf
 * @date 2018-01-12 14:18:48
 */
@CacheConfig(cacheNames = "reports")
public interface ReportRepository extends JpaRepository<Report, Long> {

    @CachePut
    @Override
    <S extends Report> S save(S entity);

    @Cacheable
    List<Report> findByJobId(@Param("jobId") long jobId);
}
