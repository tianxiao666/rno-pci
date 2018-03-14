package com.hgicreate.rno.lte.common.service.pciafp.dto;

import lombok.Data;

/**
 * @author chen.c10
 */
@Data
public class PciAfpResultDTO {

    private String cellName;
    private String cellId;
    private Integer oldEarfcn;
    private String newEarfcn;
    private Integer oldPci;
    private String newPci;
    private Double oldInterVal;
    private Double newInterVal;
    private String remark;

}
