package com.hgicreate.rno.lte.common.model.pciafp;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author chen.c10
 */
@Data
@Entity
@Table(name = "RNO_LTE_PCI_D1_CELL")
@SequenceGenerator(name = "SEQ_RNO_LTE_PCI_D1_CELL", sequenceName = "SEQ_RNO_LTE_PCI_D1_CELL", allocationSize = 1)
public class D1Cell2Inter implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_RNO_LTE_PCI_D1_CELL")
    private Long id;
    private Long jobId;
    private String cellId;
    private Integer earfcn;
    private Double interVal;
}
