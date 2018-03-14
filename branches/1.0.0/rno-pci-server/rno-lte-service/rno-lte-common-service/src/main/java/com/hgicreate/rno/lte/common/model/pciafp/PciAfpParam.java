package com.hgicreate.rno.lte.common.model.pciafp;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author chen.c10
 */
@Data
@Entity
@Table(name = "RNO_LTE_PCI_JOB_PARAM")
@SequenceGenerator(name = "SEQ_RNO_LTE_PCI_JOB_PARAM", sequenceName = "SEQ_RNO_LTE_PCI_JOB_PARAM", allocationSize = 1)
public class PciAfpParam implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_RNO_LTE_PCI_JOB_PARAM")
    private Long id;
    private Long jobId;
    private String paramType, paramCode, paramVal;
}