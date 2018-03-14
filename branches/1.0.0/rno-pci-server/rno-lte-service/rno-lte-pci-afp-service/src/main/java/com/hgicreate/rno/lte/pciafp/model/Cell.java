package com.hgicreate.rno.lte.pciafp.model;

import lombok.Data;

/**
 * @author chen.c10
 */
@Data
public class Cell {
    private String id;
    private String name;
    private int pci;
    private int earfcn;
    private double longitude;
    private double latitude;
    private String enodebId;
}
