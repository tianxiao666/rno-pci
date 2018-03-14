package com.hgicreate.rno.lte.pciafp.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author chen.c10
 */
@Data
public class PciAfpTask implements Serializable {
    private long jobId;
    private long areaId;
    private Date begMeaTime, endMeaTime, createTime, modTime;
    private String dlFileName, finishState, status, evalType;
    private String planType;
    private String convergenceType;
    private String optimizeCells;
    private boolean checkNCell;
    private boolean exportAssoTable;
    private boolean exportMidPlan;
    private boolean exportNcCheckPlan;
    private double ks = 0.02;
    private String freqAdjType;
    private long flowDataCollectId = 0;

    private Job job;
    private Area area;
}
