package com.hgicreate.rno.lte.common.model.pciafp;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author chen.c10
 */
@Data
@Entity
@Table(name = "RNO_LTE_PCI_NC_CHECK_PLAN")
@SequenceGenerator(name = "SEQ_RNO_LTE_PCI_NC_CHECK_PLAN", sequenceName = "SEQ_RNO_LTE_PCI_NC_CHECK_PLAN", allocationSize = 1)
public class NcCheckPlanItem implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_RNO_LTE_PCI_NC_CHECK_PLAN")
    private Long id;
    private Long jobId;
    private String cellId;
    private Integer earfcn = -1;
    private Integer pci = -1;
    private Double oldInterVal = 0.0;
    private Double newInterVal = 0.0;
}
