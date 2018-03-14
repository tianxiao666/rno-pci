package com.hgicreate.rno.lte.pciafp.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class Area implements Serializable {
    private long id;
    private String name;
    private long parentId;
    private int areaLevel;
    private double lon;
    private double lat;
}
