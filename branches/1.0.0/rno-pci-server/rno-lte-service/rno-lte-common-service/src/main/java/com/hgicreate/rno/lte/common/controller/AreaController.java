package com.hgicreate.rno.lte.common.controller;

import com.hgicreate.rno.lte.common.model.Area;
import com.hgicreate.rno.lte.common.service.AreaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

/**
 * @author chen.c10
 */
@Slf4j
@CrossOrigin
@RestController
public class AreaController {

    private AreaService areaService;

    public AreaController(AreaService areaService) {
        this.areaService = areaService;
    }

    /**
     * 通过用户名和缺省地市获取区域
     */
    @RequestMapping("/getAreaByAccount")
    public List<Area> getAreaByAccount(String account, @RequestParam(defaultValue = "-1") long cityId) {
        log.debug("进入方法：getAreaByAccount。account={},cityId={}", account, cityId);
        if (account == null) {
            log.debug("用户名为空");
            return Collections.emptyList();
        }
        List<Area> areas = areaService.getAreaByAccount(account, cityId);
        log.debug("退出方法：getAreaByAccount。areas={}", areas);
        return areas;
    }

    @RequestMapping("/test/getAreaByAccount2")
    public List<Area> getAreaByAccount2(String account, @RequestParam(defaultValue = "-1") long cityId) {
        log.debug("进入方法：getAreaByAccount2。account={},cityId={}", account, cityId);
        if (account == null) {
            log.debug("用户名为空");
            return Collections.emptyList();
        }
        return areaService.getAreaByAccount(account, cityId);
    }

    @RequestMapping("/test2/getAreaByAccount3")
    public List<Area> getAreaByAccount3(String account, @RequestParam(defaultValue = "-1") long cityId) {
        log.debug("进入方法：getAreaByAccount3。account={},cityId={}", account, cityId);
        if (account == null) {
            log.debug("用户名为空");
            return Collections.emptyList();
        }
        return areaService.getAreaByAccount(account, cityId);
    }
}
