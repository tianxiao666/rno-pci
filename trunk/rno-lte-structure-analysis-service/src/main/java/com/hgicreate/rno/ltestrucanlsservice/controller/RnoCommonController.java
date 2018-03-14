package com.hgicreate.rno.ltestrucanlsservice.controller;

import com.hgicreate.rno.ltestrucanlsservice.model.Area;
import com.hgicreate.rno.ltestrucanlsservice.model.JobProfile;
import com.hgicreate.rno.ltestrucanlsservice.model.Report;
import com.hgicreate.rno.ltestrucanlsservice.service.RnoCommonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
public class RnoCommonController {
    private static final Logger logger = LoggerFactory.getLogger(RnoCommonController.class);
    private RnoCommonService rnoCommonService;

    public RnoCommonController(RnoCommonService rnoCommonService) {
        this.rnoCommonService = rnoCommonService;
    }

    @RequestMapping("/verifyUserIdentity")
    public Boolean verifyUserIdentity(String account) {
        logger.debug("获取用户城市信息。account={}", account);
        return rnoCommonService.verifyUserIdentity(account);
    }

    @RequestMapping("/getAreaByAccount")
    public Map<String, List<Area>> getAreaByAccount(String account, @RequestParam(defaultValue = "-1") long currentCityId) {
        logger.debug("获取用户城市信息。account={},currentCityId={}", account, currentCityId);
        if (account == null) {
            logger.debug("用户名为空");
            return Collections.emptyMap();
        }
        return rnoCommonService.getAreaByAccount(account, currentCityId);
    }

    /**
     * 根据父区域ID，获取指定类型的子区域列表
     */
    @RequestMapping("/getSubAreaByParent")
    public List<Area> getSubAreaByParent(String account, long parentAreaId, String subAreaLevel) {
        logger.debug("进入getSubAreaByParentAreaForAjaxAction方法,parentAreaId={},subAreaLevel={}", parentAreaId, subAreaLevel);
        return rnoCommonService.getSubAreaByParent(account, parentAreaId, subAreaLevel);
    }

    /**
     * 添加一条任务信息
     */
    @PostMapping("/addOneJob")
    public Long addOneJob(JobProfile job) {
        logger.debug("进入方法：addOneJob。job={}", job);
        return rnoCommonService.addOneJob(job);
    }

    /**
     * 终止任务
     */
    @PutMapping("/stopJob/{jobId}")
    public void stopJob(@PathVariable("jobId") long jobId) {
        logger.info("进入方法：stopJob。jobId={}", jobId);
        rnoCommonService.stopJobByJobId(jobId);
    }

    /**
     * 查询指定job的报告
     */
    @GetMapping("/queryReportCnt/{jobId}")
    public Long queryReportCnt(@PathVariable("jobId") long jobId) {
        logger.debug("queryJobReportCnt.jobId={}", jobId);
        return rnoCommonService.queryReportCnt(jobId);
    }

    /**
     * 查询指定job的报告
     */
    @GetMapping("/queryReportByPage/{jobId}")
    public List<Report> queryReportByPage(@PathVariable("jobId") long jobId, int start, int end) {
        logger.debug("queryReportByPage.jobId={},start={},end={}", jobId, start, end);
        Map<String, Object> map = new HashMap<>();
        map.put("jobId", jobId);
        map.put("start", start);
        map.put("end", end);
        return rnoCommonService.queryReportByPage(map);
    }
}
