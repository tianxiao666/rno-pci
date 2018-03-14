package com.hgicreate.rno.lte.common.model;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author chen.c10
 */
@Data
@Entity
@Table(name = "RNO_SYS_USERS")
public class User implements Serializable {
    @Id
    @GeneratedValue(generator = "UserSeq")
    @SequenceGenerator(name = "UserSeq", sequenceName = "SEQ_RNO_SYS_USERS", allocationSize = 1)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    private String password, fullName, email;

    private Integer enabled, defaultCity, type;

    private Date createTime, updateTime;

    @ManyToOne
    @JoinColumn(name = "defaultCity", insertable = false, updatable = false)
    private Area area;
}
