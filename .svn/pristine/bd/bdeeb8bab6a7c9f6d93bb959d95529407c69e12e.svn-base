package com.hgicreate.rno.lte.common.repo;

import com.hgicreate.rno.lte.common.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author chen.c10
 */
public interface ReportRepository extends JpaRepository<Report, Long> {
    /**
     * 通过job id查询报告列表
     *
     * @param jobId job id
     * @return 返回报告列表
     */
    List<Report> findByJobId(long jobId);
}
