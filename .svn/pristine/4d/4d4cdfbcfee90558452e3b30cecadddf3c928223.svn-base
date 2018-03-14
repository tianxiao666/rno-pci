package com.hgicreate.rno.lte.common.service;

import com.hgicreate.rno.lte.common.model.Report;
import com.hgicreate.rno.lte.common.repo.ReportRepository;
import com.hgicreate.rno.lte.common.service.dto.ReportDTO;
import com.hgicreate.rno.lte.common.service.mapper.ReportMapper;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author chen.c10
 */
@CacheConfig(cacheNames = "reports")
@Service
public class ReportService {

    private final ReportRepository reportRepository;

    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    @Cacheable
    public List<Report> getReportsByJobId(long jobId) {
        return reportRepository.findByJobId(jobId);
    }

    @Cacheable
    public List<ReportDTO> getReportsByJobId2(long jobId) {
        return ReportMapper.INSTANCE.toDto(reportRepository.findByJobId(jobId));
    }

    @CachePut
    public Report saveReport(Report report) {
        return reportRepository.save(report);
    }
}