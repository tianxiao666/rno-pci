package com.hgicreate.rno.lte.pciafp.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class DataCollectRecord implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long dataCollectId;
    private Date uploadTime;
    private Date businessTime;
    private String fileName;
    private String oriFileName;
    private String account;
    private Long cityId;
    private Integer businessDataType;
    private Long fileSize;
    private String fullPath;
    private String fileStatus;
    private Long jobId;
    private Date launchTime;
    private Date completeTime;
}
