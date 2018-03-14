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
@Table(name = "RNO_LTE_JOB")
@SequenceGenerator(name = "rno_lte_job_id_seq", sequenceName = "rno_lte_job_id_seq", allocationSize = 1)
public class Job implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rno_lte_job_id_seq")
    private Long jobId;
    @Column(nullable = false)
    private String jobName;
    @Column(nullable = false)
    private String creator;
    @Column(nullable = false)
    private String jobType;
    private Integer priority;
    private Date createTime;
    private Timestamp launchTime, completeTime;
    private String description, jobRunningStatus, status;

    @PrePersist
    public void prePersist() {
        createTime = new Date();
        jobRunningStatus = JobStatus.Waiting.toString();
        status = "N";
        priority = 1;
    }

    public boolean canStop() {
        return JobStatus.Waiting.toString().equalsIgnoreCase(jobRunningStatus) || JobStatus.Running.toString().equalsIgnoreCase(jobRunningStatus);
    }

    public void stop() {
        jobRunningStatus = JobStatus.Stopped.toString();
    }
}
