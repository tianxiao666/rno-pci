package com.hgicreate.rno.lte.common.model.pciafp;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author chen.c10
 */
@Data
@Entity
@Table(name = "RNO_LTE_PCI_ASSO")
@SequenceGenerator(name = "SEQ_RNO_LTE_PCI_ASSO", sequenceName = "SEQ_RNO_LTE_PCI_ASSO", allocationSize = 1)
public class Cell2Rela implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_RNO_LTE_PCI_ASSO")
    private Long id;
    private Long jobId;
    private String cellId;
    private Double relaVal;
}
