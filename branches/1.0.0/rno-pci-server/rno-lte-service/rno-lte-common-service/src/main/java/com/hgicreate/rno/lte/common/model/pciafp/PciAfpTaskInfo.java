package com.hgicreate.rno.lte.common.model.pciafp;

import lombok.Data;

import java.util.Arrays;
import java.util.List;

/**
 * @author chen.c10
 */
@Data
public class PciAfpTaskInfo {
    private String account;
    private long jobId;
    private long cityId;
    private String dlFileName;
    private String finishState;
    private String taskName;
    private String taskDesc;
    private long provinceId;
    private String cityName;
    private String provinceName;
    private String begMeaTime;
    private String endMeaTime;
    private String planType;
    /**
     * 收敛方式类型
     */
    private String convergenceType;
    private String optimizeCells;
    private boolean checkNCell;
    private boolean exportAssoTable;
    private boolean exportMidPlan;
    private boolean exportNcCheckPlan;
    private boolean useFlow;
    private boolean useSf;
    private double ks = 0.02;
    /**
     * 扫频文件名串
     */
    private String sfFiles;
    /**
     * 频率调整方案类型
     */
    private String freqAdjType;
    /**
     * d1频点
     */
    private String d1Freq;
    /**
     * d2频点
     */
    private String d2Freq;
    private String sourceType;
    /**
     * 干扰矩阵文件ID
     */
    private long matrixDataCollectId = 0;
    /**
     * 流量文件ID
     */
    private long flowDataCollectId = 0;
    /**
     * 已有干扰矩阵ID
     */
    private int matrixType = 0;

    public boolean isZipResultFile() {
        return !(!this.isExportAssoTable() && !this.isExportMidPlan()
                && !this.isExportNcCheckPlan());
    }

    public List<String> getsfFilenames() {
        return Arrays.asList(null == sfFiles ? new String[0]
                : sfFiles.trim().split(","));
    }
}
