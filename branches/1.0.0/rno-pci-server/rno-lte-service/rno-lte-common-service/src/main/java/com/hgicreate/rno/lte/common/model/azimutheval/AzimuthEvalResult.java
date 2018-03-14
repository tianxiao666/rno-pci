package com.hgicreate.rno.lte.common.model.azimutheval;

import com.hgicreate.rno.lte.common.model.Area;
import com.hgicreate.rno.lte.common.model.Job;
import com.hgicreate.rno.lte.common.model.LteCell;
import lombok.Data;

import javax.persistence.*;

/**
 * @author chen.c10
 */
@Data
@Entity
@Table(name = "RNO_LTE_AZIMUTH_EVAL_RES")
@SequenceGenerator(name = "rno_lte_azimuth_eval_res_id_seq", sequenceName = "rno_lte_azimuth_eval_res_id_seq", allocationSize = 1)
public class AzimuthEvalResult {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rno_lte_azimuth_eval_res_id_seq")
    private Long id;
    private Long jobId;
    @Column(name = "cell_id", nullable = false)
    private String cellId;
    @Column(name = "city_id", nullable = false)
    private Integer cityId;
    private Integer azimuth;
    private Integer azimuth1;
    private Integer diff;

    @OneToOne
    @JoinColumn(name = "cell_id", insertable = false, updatable = false)
    private LteCell lteCell;

    @OneToOne
    @JoinColumn(name = "city_id", insertable = false, updatable = false)
    private Area area;

}
