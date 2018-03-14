package com.hgicreate.rno.lte.common.model;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * @author chen.c10
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

    /*@OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "parentId")
    @OrderBy("id")
    private List<Area> children;*/
}
