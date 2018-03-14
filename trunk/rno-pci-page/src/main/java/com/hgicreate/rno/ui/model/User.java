package com.hgicreate.rno.ui.model;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class User {
    private int id;
    private String account;
    private String name;
    private int type;
    private int status;
    private Timestamp createTime;
    private Timestamp updateTime;
    private String email;
    private String password;
    private int own_city;
    private int default_city;
}
