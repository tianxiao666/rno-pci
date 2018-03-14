package com.hgicreate.rno.model;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 区域表
 *
 * @author chen.c10
 * @date 2018-01-12 14:04:33
 */
@Data
@Entity
@Table(name = "RNO_SYS_AREA")
@Cacheable
public class Area implements Serializable {

    @Id
    @Column(name = "id")
    private Long id;
    @Column(nullable = false)
    private String name;
    private Long parentId;
    private Integer areaLevel;
    @Column(name = "longitude")
    private Double lon;
    @Column(name = "latitude")
    private Double lat;

}
