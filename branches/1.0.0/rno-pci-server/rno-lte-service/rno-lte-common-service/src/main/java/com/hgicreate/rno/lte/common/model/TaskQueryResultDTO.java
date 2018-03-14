package com.hgicreate.rno.lte.common.model;

import com.hgicreate.rno.lte.common.model.azimutheval.AzimuthEvalTask;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Data
@NoArgsConstructor
public class TaskQueryResultDTO {

    private String cityName;
    private long jobId;
    private String jobName;
    private String jobRunningStatus;
    private String begMeaTime;
    private String endMeaTime;
    private String launchTime;
    private String completeTime;
    private String createTime;

    public TaskQueryResultDTO(AzimuthEvalTask task) {
        this.cityName = task.getArea().getName();
        this.jobId = task.getJobId();
        this.jobName = task.getJob().getJobName();
        this.jobRunningStatus = task.getJob().getJobRunningStatus();
        this.begMeaTime = task.getBegMeaTime() == null ? "" : DateToString(task.getBegMeaTime(), "yyyy-MM");
        this.endMeaTime = task.getEndMeaTime() == null ? "" : DateToString(task.getEndMeaTime(), "yyyy-MM");
        this.launchTime = task.getJob().getLaunchTime() == null ? "" : DateToString(task.getJob().getLaunchTime(), "yyyy-MM-dd HH:mm:ss");
        this.completeTime = task.getJob().getCompleteTime() == null ? "" : DateToString(task.getJob().getCompleteTime(), "yyyy-MM-dd HH:mm:ss");
        this.createTime = task.getCreateTime() == null ? "" : DateToString(task.getCreateTime(), "yyyy-MM-dd HH:mm:ss");
    }

    private String DateToString(Date date, String pattern) {
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime time = instant.atZone(zoneId).toLocalDateTime();
        return time.format(DateTimeFormatter.ofPattern(pattern));
    }
}
