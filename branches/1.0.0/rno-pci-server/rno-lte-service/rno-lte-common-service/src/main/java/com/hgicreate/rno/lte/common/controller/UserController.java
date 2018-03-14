package com.hgicreate.rno.lte.common.controller;

import com.hgicreate.rno.lte.common.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * @author chen.c10
 */
@Slf4j
@CrossOrigin
@RestController
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Deprecated
    @RequestMapping("/verifyUserIdentity")
    public Boolean verifyUserIdentity(@RequestParam(value = "account", required = false) String username) {
        log.debug("进入方法：verifyUserIdentity。username={}", username);
        if (StringUtils.isBlank(username)) {
            log.debug("用户名为空");
            return false;
        }
        return userService.existsByUsername(username);
    }

    @RequestMapping("/validateUser")
    public Boolean validateUser(@RequestParam(value = "username", required = false) String username) {
        log.debug("进入方法：validateUser。username={}", username);
        if (StringUtils.isBlank(username)) {
            log.debug("用户名为空");
            return false;
        }
        return userService.existsByUsername(username);
    }
}
