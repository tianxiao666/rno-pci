package com.hgicreate.rno.controller;

import com.hgicreate.rno.service.LoginRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class RnoMsManagePlatFormController {

    private static final Logger logger = LoggerFactory.getLogger(RnoMsManagePlatFormController.class);
    @Value("${HOST_IP}")
    private String HOST_IP;
    @Value("${PCI-EVAL.PORT}")
    private String PCI_EVAL_PORT;
    @Value("${INTER-MATRIX.PORT}")
    private String INTER_MATRIX_PORT;
    @Value("${PCI-AFP.PORT}")
    private String PCI_AFP_PORT;
    @Value("${DYNAMIC-COVER.PORT}")
    private String DYNAMIC_COVER_PORT;
    @Value("${NETWORK-COVER.PORT}")
    private String NETWORK_COVER_PORT;
    @Value("${STRUCT-ANA.PORT}")
    private String STRUCT_ANA_PORT;
    @Value("${AZIMUTH-EVAL.PORT}")
    private String AZIMUTH_EVAL_PORT;

    //	private String account = "liu.yp@iscreate.com";
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private LoginRecordService loginRecordService;

    @RequestMapping("/")
    public String redirectServices(HttpServletRequest request, HttpServletResponse response,
                                   @RequestParam("msId") long msId) {
        String account = request.getParameter("account");
        logger.debug("进入重定向微服务模式，msId={},account={}", msId, account);
        String POST_URL = "";
        String res = "";
        if (account == null || "".equals(account)) {

            logger.debug("非法用户登录，请警剔！");
            res = "Sorry ,未经授权，非法用户登录！";
            return res;

        } else {
            //邮箱格式规则判断
            Pattern p = Pattern.compile("^([a-zA-Z0-9_-|.])+@([a-zA-Z0-9_-])+(\\.([a-zA-Z0-9_-])+)+$");
            Matcher m = p.matcher(account);
            //Mather m = p.matcher("fdfdfd@gmail.com.cn");这种也是可以的！
            boolean b = m.matches();
            if(!b){
                logger.debug("帐户信息不符合格式要求！");
                res = "Sorry ,帐户信息不符合格式要求！";
                return res;
            }
        }
        if (msId == 1) {

            logger.debug("访问小区PCI评估页面 进行了前后端分离，msId={},account={}", msId, account);
            POST_URL = "http://" + HOST_IP + ":" + PCI_EVAL_PORT + "/";

        } else if (msId == 2) {

            logger.debug("访问干扰矩阵计算页面，msId={},account={}", msId, account);
            POST_URL = "http://" + HOST_IP + ":" + INTER_MATRIX_PORT + "/";

        } else if (msId == 3) {

            logger.debug("访问区域PCI翻频页面，msId={},account={}", msId, account);
            POST_URL = "http://" + HOST_IP + ":" + PCI_AFP_PORT + "/";

        } else if (msId == 4) {

            logger.debug("访问LTE动态覆盖图页面 进行前后端分离，msId={},account={}", msId, account);
            POST_URL = "http://" + HOST_IP + ":" + DYNAMIC_COVER_PORT + "/";

        } else if (msId == 5) {

            logger.debug("访问LTE网络结构分析页面 进行前后端分离，msId={},account={}", msId, account);
            POST_URL = "http://" + HOST_IP + ":" + NETWORK_COVER_PORT + "/";

        } else if (msId == 6) {
            logger.debug("访问LTE网络结构优化页面 进行前后端分离，msId={},account={}", msId, account);
            POST_URL = "http://" + HOST_IP + ":" + STRUCT_ANA_PORT + "/";
        } else if (msId == 7) {

            logger.debug("访问天线方位角评估页面，msId={},account={}", msId, account);
            POST_URL = "http://" + HOST_IP + ":" + AZIMUTH_EVAL_PORT + "/";

        } else {

            logger.debug("您访问的微服务标识页面不存在，msId={},account={}", msId, account);
            res = "Sorry " + account + " ,该" + msId + "标识页面不存在,请确认后重新访问！";
            return res;

        }
        // 创建日期
        Calendar cal = Calendar.getInstance();
        cal = Calendar.getInstance();
        String createTime = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss").format(cal.getTime());
        /*
        信息记录规则
		如果存在该帐户则勿须增加帐户及组用户信息
		如果不存该帐户信息则增加帐户及组织用户信息【sys_account，sys_org_user】
		先加组织用户信息组织用户名统用-网优用户，组织email用rno@hgicreate.com，插入组织用户获取ID，再添加帐户信息，个人email，密码，组织用户ID，创建时间
		*/
        List<Map<String, Object>> users = loginRecordService.getUserByAccount(account);
        Map<String, Object> loginInfoCond = new HashMap<String, Object>();
        String orgUserId = "";
        long orgId = 0;
        if (users == null || users.size() == 0) {
            //增加组织用户
            loginInfoCond.put("name", "网优用户");
//			loginInfoCond.put("email","rno@hgicreate.com");
            loginInfoCond.put("email", account);
            loginRecordService.addOrgUserInfo(loginInfoCond);

            users = loginRecordService.getOrgUserByAccount(account);
            orgUserId = users.get(0).get("ORG_USER_ID").toString();
            loginInfoCond.clear();
            //增加用户信息
            loginInfoCond.put("account", account);
            loginInfoCond.put("password", "888");
            loginInfoCond.put("org_user_id", orgUserId);
            loginInfoCond.put("createtime", createTime);
            loginInfoCond.put("updatetime", createTime);
            loginRecordService.addAccountInfo(loginInfoCond);
            //保存用户与岗位关系【SYS_USER_RELA_POST】
            //默认使用网络优化技术部的组织ID
            orgId = loginRecordService.selectOrgIdByName();
            loginInfoCond.clear();
            loginInfoCond.put("org_id", orgId);
            loginInfoCond.put("org_user_id", orgUserId);
            loginInfoCond.put("createtime", createTime);
            loginInfoCond.put("start_time", createTime);
            loginInfoCond.put("end_time", createTime);
            loginInfoCond.put("status","A");
            loginRecordService.saveSysUserRelaPost(loginInfoCond);
            //保存用户与组织关系【SYS_USER_RELA_ORG】
            loginInfoCond.clear();
            loginInfoCond.put("org_id", orgId);
            loginInfoCond.put("org_user_id", orgUserId);
            loginInfoCond.put("createtime", createTime);
            loginInfoCond.put("update_time", createTime);
            loginInfoCond.put("status","A");
            loginRecordService.saveSysUserRelaOrg(loginInfoCond);
        }
        /*规则：只要通过该接口访问微服务信息则加入审计信息【sys_security_loginrecord】*/
        Long lastAccessedTime0 = request.getSession().getLastAccessedTime();
        Timestamp lastAccessTime = new Timestamp(lastAccessedTime0);
        String remoteAddr = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        StringTokenizer st = new StringTokenizer(userAgent, ";");
        String userBrowser = st.nextToken();

        loginInfoCond.clear();
        loginInfoCond.put("account", account);
        loginInfoCond.put("login_time", createTime);
        loginInfoCond.put("browser", userBrowser);
        loginInfoCond.put("ip", remoteAddr);
        loginInfoCond.put("last_login_time", createTime);
        loginRecordService.addLoginInfo(loginInfoCond);
        //重定向URL
        try {
            response.sendRedirect(POST_URL+"?account="+account);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //获取资源
//        res = restTemplate.getForObject(POST_URL+"/{account}", String.class, account);
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("account",account);
        res = restTemplate.postForObject(POST_URL,params,String.class);
        logger.debug("redirectServices.res={}", res.length());
        return res;
    }

}
