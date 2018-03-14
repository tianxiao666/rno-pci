package com.hgicreate.rno.web.rest.dto;

import com.hgicreate.rno.model.Report;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * 报告传输对象
 *
 * @author li.tf
 * @date 2018-01-12 13:56:22
 */
@Data
public class ReportDTO {

    private String stage;
    private String startTime;
    private String endTime;
    private String result;
    private String detail;

    public ReportDTO(Report report) {
        this.stage = report.getStage();
        this.startTime = datetostring(report.getBegTime());
        this.endTime = datetostring(report.getEndTime());
        this.result = report.getState();
        this.detail = report.getAttMsg();
    }

    private String datetostring(Date date) {
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime time = instant.atZone(zoneId).toLocalDateTime();
        return time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
