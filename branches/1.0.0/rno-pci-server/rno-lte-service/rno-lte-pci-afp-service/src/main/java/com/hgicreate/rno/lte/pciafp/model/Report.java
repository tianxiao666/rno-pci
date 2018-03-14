package com.hgicreate.rno.lte.pciafp.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class Report implements Serializable {
    private long jobId;
    private long reportId;
    private String stage;
    private Date begTime;
    private Date endTime;
    private String state;
    private String attMsg = "";
    private Integer reportType = 1;

    public void setSystemFields(String stage, Date begTime, Date endTime, String state, String attMsg) {
        this.stage = stage;
        this.begTime = begTime;
        this.endTime = endTime;
        this.state = state;
        this.attMsg = attMsg;
        this.reportType = 2;// 系统类型
    }

    public void setFields(String stage, Date begTime, Date endTime, String state, String attMsg) {
        this.stage = stage;
        this.begTime = begTime;
        this.endTime = endTime;
        this.state = state;
        this.attMsg = attMsg;
        this.reportType = 1;// 业务类型
    }
}
