package com.hgicreate.rno.model;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 报告表
 *
 * @author chen.c10
 * @date 2018-01-12 14:11:22
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
}
