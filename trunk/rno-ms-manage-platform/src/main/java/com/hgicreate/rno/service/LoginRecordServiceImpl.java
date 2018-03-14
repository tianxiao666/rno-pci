package com.hgicreate.rno.service;


import com.hgicreate.rno.mapper.LoginRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by chao.xj on 2016/12/30.
 */
@Service
@ComponentScan
public class LoginRecordServiceImpl implements LoginRecordService {
    @Autowired
    private LoginRecordMapper loginRecordMapper;

    @Override
    public void addAccountInfo(Map<String,Object> map) {
         loginRecordMapper.insertAccountInfo(map);
    }

    public void addLoginInfo(Map<String,Object> map) {
        loginRecordMapper.insertLoginInfo(map);
    }

    public void addOrgUserInfo(Map<String,Object> map) {
        loginRecordMapper.insertOrgUserInfo(map);
    }

    public void saveSysUserRelaPost(Map<String,Object> map) {
        loginRecordMapper.saveSysUserRelaPost(map);
    }

    public void saveSysUserRelaOrg(Map<String,Object> map) {
        loginRecordMapper.saveSysUserRelaOrg(map);
    }

    @Override
    public List<Map<String,Object>> getUserByAccount(String account) {
        return loginRecordMapper.selectUserByAccount(account);
    }

    @Override
    public List<Map<String,Object>> getOrgUserByAccount(String account) {
        return loginRecordMapper.selectOrgUserByAccount(account);
    }

    public long selectOrgIdByName(){
        return loginRecordMapper.selectOrgIdByName();
    }
}
