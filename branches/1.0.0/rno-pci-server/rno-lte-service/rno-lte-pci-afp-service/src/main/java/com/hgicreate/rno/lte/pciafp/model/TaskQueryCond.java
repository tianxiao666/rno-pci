package com.hgicreate.rno.lte.pciafp.model;

import lombok.Data;

/**
 * 任务查询条件
 */
@Data
public class TaskQueryCond {
    private String isMine;
    private String account;
    private String jobType;
    private long cityId;
    private String taskName;
    private String taskStatus;
    private String meaTime;
    private String startSubmitTime;
    private String endSubmitTime;
    private int start;
    private int end;
}
