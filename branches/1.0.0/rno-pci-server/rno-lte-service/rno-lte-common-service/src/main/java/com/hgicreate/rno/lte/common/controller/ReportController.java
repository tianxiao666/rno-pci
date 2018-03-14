package com.hgicreate.rno.lte.common.controller;

import com.hgicreate.rno.lte.common.model.Report;
import com.hgicreate.rno.lte.common.service.ReportService;
import com.hgicreate.rno.lte.common.service.dto.ReportDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author chen.c10
 */
@Slf4j
@CrossOrigin
@RestController
public class ReportController {

    private ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    /**
     * 通过jobId获取报告
     */
    @GetMapping("/query-report/{jobId}")
    public List<ReportDTO> getReportsByJobId(@PathVariable("jobId") long jobId) {
        log.debug("进入方法：getReportsByJobId。jobId={}", jobId);
        return reportService.getReportsByJobId2(jobId);
    }

    /**
     * 增加一条新的报告记录
     */
    @PostMapping("/saveReport")
    public Report saveReport(@RequestBody Report report) {
        log.debug("进入方法:saveReport。report = {}", report);
        return reportService.saveReport(report);
    }
}
