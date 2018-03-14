package com.hgicreate.rno.service;


import java.util.List;
import java.util.Map;

/**
 * Created by chao.xj on 2016/12/30.
 */

public interface LoginRecordService {
    /*
    * 将用户信息加入sys_account表中
    * */
    public void addAccountInfo(Map<String,Object> map);

    /*
    * 将用户登入信息加入sys_security_loginrecord表中
    * */
    public void addLoginInfo(Map<String,Object> map);

    /*
    * 将组织用户信息加入sys_org_user表中
    * */
    public void addOrgUserInfo(Map<String,Object> map);
    /*
    保存用户和岗位关系
     */
    public void saveSysUserRelaPost(Map<String,Object> map);
    /*
    保存用户和组织关系
     */
    public void saveSysUserRelaOrg(Map<String,Object> map);
    /*
        查询用户信息
    */
    public List<Map<String,Object>> getUserByAccount(String account);

    /*
        查询用户信息
    */
    public List<Map<String,Object>> getOrgUserByAccount(String account);
    /*
        查询组织ID（网络优化技术部）
     */
    public long selectOrgIdByName();
}
