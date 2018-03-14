package com.hgicreate.rno.lte.pciafp.model;

import lombok.Data;

@Data
public class Cell2Rela {
    private long id;
    private long jobId;
    private int planNum;
    private String cellId;
    private double relaVal;
}
