package com.hgicreate.rno.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

/**
 * 方位角评估任务表
 *
 * @author chen.c10
 * @date 2018-01-12 14:06:22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "RNO_LTE_AZIMUTH_EVAL_JOB")
public class AzimuthEvalTask extends AbstractTask {
    @Id
    private Long jobId;
    @Column(name = "city_id", nullable = false)
    private Long areaId;
    private Date begMeaTime, endMeaTime, modTime;
    private Timestamp createTime;
    private String dlFileName, finishState, status, evalType;
    private String cells;

    @OneToOne(cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private Job job;

    @OneToOne
    @JoinColumn(name = "city_id", insertable = false, updatable = false)
    private Area area;

    @PrePersist
    public void prePersist() {
        Date now = new Date();
        createTime = new Timestamp(System.currentTimeMillis());
        modTime = now;
        finishState = "排队中";
        status = "N";
    }

    @PreUpdate
    public void preUpdate() {
        modTime = new Date();
    }
}
