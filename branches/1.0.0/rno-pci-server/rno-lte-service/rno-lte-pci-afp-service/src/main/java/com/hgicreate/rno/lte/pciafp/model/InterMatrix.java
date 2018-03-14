package com.hgicreate.rno.lte.pciafp.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author chen.c10
 */
@Data
public class InterMatrix implements Serializable {
    private String cellId;
    private String ncellId;
    private Double relaVal;
    private Integer cellPci;
    private Integer ncellPci;
    private Integer cellEarfcn;
    private Integer ncellEarfcn;
    private Double mrRela;
    private Double hoRela;
    private Double sfRela;
}
