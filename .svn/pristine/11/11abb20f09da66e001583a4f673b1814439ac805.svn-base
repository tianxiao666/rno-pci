package com.hgicreate.rno.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * 小区表
 *
 * @author li.tf
 * @date 2018:09:22
 */
@Data
@Entity
@Table(name = "RNO_LTE_CELL")
class LteCell implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String cellId;
    private String cellName;
    private String longitude;
    private String latitude;
    private String azimuth;
    private String earfcn;
    private String pci;
    private String enodebId;

}
