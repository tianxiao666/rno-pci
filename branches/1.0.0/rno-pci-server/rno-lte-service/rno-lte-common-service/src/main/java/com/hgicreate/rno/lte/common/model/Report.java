package com.hgicreate.rno.lte.common.model;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

/**
 * @author chen.c10
 */
@Data
@Entity
@Table(name = "RNO_LTE_JOB_REPORT")
@SequenceGenerator(name = "rno_lte_job_report_id_seq", sequenceName = "rno_lte_job_report_id_seq", allocationSize = 1)
public class Report implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rno_lte_job_report_id_seq")
    private Long reportId;
    private Long jobId;
    private String stage;
    private Timestamp begTime;
    private Timestamp endTime;
    private String state;
    private Integer reportType = 1;
    private String attMsg = "";

    public void setSystemFields(String stage, Timestamp begTime, Timestamp endTime, String state, String attMsg) {
        this.stage = stage;
        this.begTime = begTime;
        this.endTime = endTime;
        this.state = state;
        this.attMsg = attMsg;
        // 系统类型
        this.reportType = 2;
    }

    public void setFields(String stage, Timestamp begTime, Timestamp endTime, String state, String attMsg) {
        this.stage = stage;
        this.begTime = begTime;
        this.endTime = endTime;
        this.state = state;
        this.attMsg = attMsg;
        // 业务类型
        this.reportType = 1;
    }
}
