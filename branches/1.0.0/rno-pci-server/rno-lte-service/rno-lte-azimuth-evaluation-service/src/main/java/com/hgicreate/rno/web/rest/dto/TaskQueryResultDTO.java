package com.hgicreate.rno.web.rest.dto;

import com.hgicreate.rno.model.AzimuthEvalTask;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * 任务信息结果传输对象
 *
 * @author li.tf
 * @date 2018-01-12 13:57:33
 */
@Data
public class TaskQueryResultDTO {

    private String cityName;
    private Long jobId;
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
        this.begMeaTime = task.getBegMeaTime() == null ? "" : datetostring(task.getBegMeaTime(), "yyyy-MM");
        this.endMeaTime = task.getEndMeaTime() == null ? "" : datetostring(task.getEndMeaTime(), "yyyy-MM");
        this.launchTime = task.getJob().getLaunchTime() == null ? "" : datetostring(task.getJob().getLaunchTime(), "yyyy-MM-dd HH:mm:ss");
        this.completeTime = task.getJob().getCompleteTime() == null ? "" : datetostring(task.getJob().getCompleteTime(), "yyyy-MM-dd HH:mm:ss");
        this.createTime = task.getCreateTime() == null ? "" : datetostring(task.getCreateTime(), "yyyy-MM-dd HH:mm:ss");
    }

    private String datetostring(Date date, String pattern) {
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime time = instant.atZone(zoneId).toLocalDateTime();
        return time.format(DateTimeFormatter.ofPattern(pattern));
    }
}
