package com.hgicreate.rno.lte.common.model.pciafp;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author chen.c10
 */
@Data
@Entity
@Table(name = "RNO_LTE_THRESHOLD")
public class Threshold implements Serializable {
    @Id
    private Long id;
    private Long orderNum;
    private String moduleType, code, descInfo, defaultVal, scopeDesc, conditionGroup;
}