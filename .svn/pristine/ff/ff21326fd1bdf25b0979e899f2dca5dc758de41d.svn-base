package com.hgicreate.rno.ui.controller;

import com.hgicreate.rno.ui.model.LoginRecord;
import com.hgicreate.rno.ui.model.User;
import com.hgicreate.rno.ui.service.LoginRecordService;
import com.hgicreate.rno.ui.service.UserService;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

@Controller
public class PciPageController {

    private static final Logger logger = LoggerFactory.getLogger(PciPageController.class);

    private final UserService userService;
    private final LoginRecordService loginRecordService;

    public PciPageController(UserService userService, LoginRecordService loginRecordService) {
        this.userService = userService;
        this.loginRecordService = loginRecordService;
    }

    /**
     * 获取用户信息，校检登录
     */
    @RequestMapping("/checkLogin")
    @ResponseBody
    Boolean checkLogin(ServletRequest request, ServletResponse response) throws IOException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        //获取用户登入信息,存入审计表
        Long lastAccessedTime0 = req.getSession().getLastAccessedTime();
        Timestamp lastAccessTime = new Timestamp(lastAccessedTime0);
        String remoteAddr = request.getRemoteAddr();
        String userAgent = req.getHeader("User-Agent");
        StringTokenizer st = new StringTokenizer(userAgent, ";");
        String userBrowser = st.nextToken();

        LoginRecord record = new LoginRecord();
        record.setAccount(req.getParameter("account"));
        record.setBrowser(userBrowser);
        record.setHttpReferer(req.getHeader("REFERER"));
        record.setIp(remoteAddr);
        record.setLoginTime(lastAccessTime);

        //避免数据库重复加入审计信息
        List<LoginRecord> loginRecordList = loginRecordService.getAllLoginRecords();
        String browser = "";
        String loginAccount = "";
        for (Iterator<LoginRecord> iter = loginRecordList.iterator(); iter.hasNext(); ) {
            LoginRecord loginRecord = iter.next();
            browser = loginRecord.getBrowser();
            loginAccount = loginRecord.getAccount();
        }
        if (!record.getAccount().equals(loginAccount) || !record.getBrowser().equals(browser)) {
            loginRecordService.addLoginInfo(record);
        }

        //校验登录
        String account = req.getParameter("account").trim();
        String password = req.getParameter("password").trim();
        List<User> userList = userService.getAllUser();
        String userAccount = "";
        List<String> userAccList = new ArrayList<String>();

        for (Iterator<User> iter = userList.iterator(); iter.hasNext(); ) {
            userAccount = iter.next().getAccount();
            userAccList.add(userAccount);
        }

        //判断用户输入
        if ("".equals(account) || "".equals(password)) {
            System.out.println("账户密码均不能为空");
            try {
                req.getRequestDispatcher("/login").forward(request, response);

            } catch (ServletException e) {
                e.printStackTrace();
            }
            return false;
        } else {
            if (!userAccList.contains(account)) {
                User user = new User();
                user.setAccount(account);
                user.setPassword(password);
                userService.addUser(user);
            } else {
                System.out.println("用戶存在");
            }
            try {
                req.getRequestDispatcher("/index").forward(request, response);
            } catch (ServletException e) {
                e.printStackTrace();
            }
            return true;
        }
    }

    /**
     * 获取iframe父页面的用户名。添加到用户表
     */
    @RequestMapping(value = "receiveUserName", method = RequestMethod.POST)
    @ResponseBody
    void receiveUserName(@RequestBody String userinfo) {
        JSONObject jsonObject = JSONObject.fromObject(userinfo);
        User us = (User) JSONObject.toBean(jsonObject, User.class);
        List<User> userList = userService.getAllUser();
        String userAccount = "";
        List<String> userAccList = new ArrayList<String>();
        for (Iterator<User> iter = userList.iterator(); iter.hasNext(); ) {
            userAccount = iter.next().getAccount();
            userAccList.add(userAccount);
        }
        if (us.getAccount() != null && !userAccList.contains(us.getAccount())) {
            userService.addUser(us);
        }
    }
}
