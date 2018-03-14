package com.hgicreate.rno.lte.pciafp.model;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class Threshold implements Serializable {
    private static final long serialVersionUID = 1L;
    private BigDecimal id;
    private long orderNum;
    private String moduleType;
    private String code;
    private String descInfo;
    private String defaultVal;
    private String scopeDesc;
    private String conditionGroup;
    private boolean flag = true;
}