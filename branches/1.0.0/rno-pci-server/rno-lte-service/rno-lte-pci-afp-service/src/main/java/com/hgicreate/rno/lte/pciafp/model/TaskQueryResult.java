package com.hgicreate.rno.lte.pciafp.model;

import lombok.Data;

/**
 * 任务查询结果
 */
@Data
public class TaskQueryResult {
    private String cityName;
    private long jobId;
    private String jobName;
    private String jobRunningStatus;
    private String begMeaTime;
    private String endMeaTime;
    private String dlFileName;
    private String launchTime;
    private String completeTime;
}
