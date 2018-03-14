package com.hgicreate.rno.lte.common.model.pciafp;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author chen.c10
 */
@Data
@Entity
@Table(name = "RNO_LTE_DATA_COLLECT_REC")
@SequenceGenerator(name = "SEQ_RNO_DATA_COLLECT_REC", sequenceName = "SEQ_RNO_DATA_COLLECT_REC", allocationSize = 1)
public class DataCollectRecord implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_RNO_DATA_COLLECT_REC")
    private Long dataCollectId;
    private Long jobId;
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
}
