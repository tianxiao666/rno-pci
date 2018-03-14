package com.hgicreate.rno.lte.common.model;

import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Data
public class ReportDTO {

    private String stage;
    private String startTime;
    private String endTime;
    private String result;
    private String detail;

    public ReportDTO(Report report) {
        this.stage = report.getStage();
        this.startTime = DateToString(report.getBegTime());
        this.endTime = DateToString(report.getEndTime());
        this.result = report.getState();
        this.detail = report.getAttMsg();
    }

    private String DateToString(Date date) {
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime time = instant.atZone(zoneId).toLocalDateTime();
        return time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
