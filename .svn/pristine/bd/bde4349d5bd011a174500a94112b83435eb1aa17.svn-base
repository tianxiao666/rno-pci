package com.hgicreate.rno.lte.common.model.cellmgr;

import com.hgicreate.rno.lte.common.model.Area;
import lombok.Data;

import javax.persistence.*;

/**
 * @author chen.c10
 */
@Data
@Entity
@Table(name = "RNO_LTE_CELL")
public class Cell {
    @Id
    private String cellId;
    private String enodebId, cellName, bandType, coverType;
    private Long areaId;
    /**
     * 有空值的列
     */
    private Integer pci, azimuth, stationSpace, earfcn;
    private Double longitude, latitude;

    @ManyToOne
    @JoinColumn(name = "areaId", insertable = false, updatable = false)
    private Area area;

    @Transient
    private String areaName;

    @PostLoad
    public void postLoad() {
        if (null != area) {
            areaName = area.getName();
        }
    }
}
