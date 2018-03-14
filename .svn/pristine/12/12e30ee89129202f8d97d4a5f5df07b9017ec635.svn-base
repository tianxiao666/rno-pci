package com.hgicreate.rno.lte.common.model.pciafp;

import com.hgicreate.rno.lte.common.model.AbstractTask;
import com.hgicreate.rno.lte.common.model.Area;
import com.hgicreate.rno.lte.common.model.Job;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;

/**
 * @author chen.c10
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "RNO_LTE_PCI_JOB")
public class PciAfpTask extends AbstractTask {
    @Id
    private Long jobId;
    @Column(name = "city_id", nullable = false)
    private Long areaId;
    private Date createTime, modTime;
    private String dlFileName, finishState, status, optimizeCells;
    @Column(name = "is_check_ncell")
    private Boolean checkNCell;
    @Column(name = "ks_corr_val")
    private Double ks = 0.02;

    @OneToOne(cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private Job job;

    @OneToOne
    @JoinColumn(name = "city_id", insertable = false, updatable = false)
    private Area area;

    @PrePersist
    public void prePersist() {
        Date now = new Date();
        createTime = now;
        modTime = now;
        finishState = "排队中";
        status = "N";
    }

    @PreUpdate
    public void preUpdate() {
        modTime = new Date();
    }
}
