package com.hgicreate.rno.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Created by chao.xj on 2016/12/30.
 */
@Mapper
public interface LoginRecordMapper {

    void insertOrgUserInfo(Map<String,Object> map);

    void insertAccountInfo(Map<String,Object> map);

    void insertLoginInfo(Map<String,Object> map);

    void saveSysUserRelaPost(Map<String,Object> map);

    void saveSysUserRelaOrg(Map<String,Object> map);

    List<Map<String,Object>> selectUserByAccount(String account);

    List<Map<String,Object>> selectOrgUserByAccount(String account);

    long selectOrgIdByName();
}
