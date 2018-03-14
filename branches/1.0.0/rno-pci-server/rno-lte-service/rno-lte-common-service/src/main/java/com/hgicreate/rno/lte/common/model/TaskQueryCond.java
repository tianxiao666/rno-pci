package com.hgicreate.rno.lte.common.model;

import lombok.Data;

/**
 * @author chen.c10
 */
@Data
public class TaskQueryCond {
    private String account;
    private String jobType;
    private long cityId;
    private String taskName;
    private String taskStatus;
    //private String meaTime;
    private String startSubmitTime;
    private String endSubmitTime;
}
