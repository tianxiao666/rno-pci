package com.hgicreate.rno.model;

import lombok.Data;

import javax.persistence.*;

/**
 * 方位角评估结果表
 *
 * @author chen.c10
 * @date 2018-01-12 14:05:00
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
    private Integer azimuth = 0;
    private Integer azimuth1 = 0;
    private Integer diff = 0;

    @OneToOne
    @JoinColumn(name = "cell_id", insertable = false, updatable = false)
    private LteCell lteCell;

    @OneToOne
    @JoinColumn(name = "city_id", insertable = false, updatable = false)
    private Area area;

}
