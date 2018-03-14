package com.hgicreate.rno.lteazimuthevaluation.controller;

import com.hgicreate.rno.lteazimuthevaluation.model.*;
import com.hgicreate.rno.lteazimuthevaluation.properties.RnoProperties;
import com.hgicreate.rno.lteazimuthevaluation.service.RnoCommonService;
import com.hgicreate.rno.lteazimuthevaluation.service.RnoLteAzimuthEvaluationService;
import com.hgicreate.rno.lteazimuthevaluation.task.RnoLteAzimuthEvaluationTask;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.*;

@Controller
@SessionAttributes("account")
public class RnoLteAzimuthEvaluationController {

    private static final Logger logger = LoggerFactory.getLogger(RnoLteAzimuthEvaluationController.class);

//    private String account = "liu.yp@iscreate.com";

    private RnoCommonService rnoCommonService;

    private RnoLteAzimuthEvaluationService rnoLteAzimuthEvaluationService;

    private RnoProperties rnoProperties;

    private RnoLteAzimuthEvaluationTask rnoLteAzimuthEvaluationTask;

    public RnoLteAzimuthEvaluationController(RnoCommonService rnoCommonService, RnoLteAzimuthEvaluationService rnoLteAzimuthEvaluationService, RnoProperties rnoProperties, RnoLteAzimuthEvaluationTask rnoLteAzimuthEvaluationTask) {
        this.rnoCommonService = rnoCommonService;
        this.rnoLteAzimuthEvaluationService = rnoLteAzimuthEvaluationService;
        this.rnoProperties = rnoProperties;
        this.rnoLteAzimuthEvaluationTask = rnoLteAzimuthEvaluationTask;
    }

    /**
     * 通过账户和城市Id初始化用户区域信息
     */
    private Map<String, Object> initAreaByAccountAndDefaultCityId(String account, long cityId) {
        Map<String, Object> model = new HashMap<>();
        Map<String, List<Area>> map = rnoCommonService.getAreaByAccountAndCityId(account, cityId);
        model.put("provinces", map.get("provinceAreas"));
        model.put("cities", map.get("cityAreas"));
        return model;
    }

    /**
     * 访问首页
     */
    @RequestMapping("/")
    public String indexGet(Map<String, Object> model, String account) {
        logger.debug("访问首页。account={},cityId={}", account);

        if (!rnoCommonService.verifyUserIdentity(account)) {
            model.put("error", "非法登录用户，请选择正常渠道登录，谢谢！");
            return "rno_fail";
        }

        Map<String, List<Area>> map = rnoCommonService.getAreaByAccountAndCityId(account, 0);
        List<Area> provinceAreas = map.get("provinceAreas");
        if (null == provinceAreas || provinceAreas.isEmpty()) {
            model.put("error", "当前用户没有足够的权限！");
            return "rno_fail";
        }
        model.put("provinces", provinceAreas);
        model.put("cities", map.get("cityAreas"));

        model.put("account", account);
        return "rno_lte_azimuth_evaluation";
    }

    /**
     * 返回首页
     */
    @PostMapping("/backIndex")
    public String indexPost(Map<String, Object> model, @ModelAttribute("account") String account, @RequestParam(defaultValue = "0") long cityId) {
        logger.debug("访问首页。account={},cityId={}", account, cityId);
        model.putAll(initAreaByAccountAndDefaultCityId(account, cityId));
        return "rno_lte_azimuth_evaluation";
    }

    /**
     * 跳转到任务信息页面
     */
    @RequestMapping("/createTask")
    public String createTask(Map<String, Object> model, @ModelAttribute("account") String account, @RequestParam(defaultValue = "0") long cityId) {
        logger.debug("进入createTask方法。account={},cityId={}", account, cityId);
        model.putAll(initAreaByAccountAndDefaultCityId(account, cityId));
        return "rno_lte_azimuth_task_info";
    }

    /**
     * 根据父区域ID，获取指定类型的子区域列表
     */
    @RequestMapping("/getSubAreaByParent")
    @ResponseBody
    public List<Area> getSubAreaByParent(@ModelAttribute("account") String account, long parentAreaId, String subAreaLevel) {
        logger.debug("进入getSubAreaByParent方法,parentAreaId={},subAreaLevel={}", parentAreaId, subAreaLevel);
        return rnoCommonService.getSubAreaByParent(account, parentAreaId, subAreaLevel);
    }

    /**
     * 查询Lte方位角计算任务
     */
    @RequestMapping("/queryLteAzimuthEvaluationTaskByPage")
    @ResponseBody
    public Map<String, Object> queryLteAzimuthEvaluationTaskByPage(@ModelAttribute("account") String account, TaskQueryCond cond, Page page) {
        logger.debug("进入：queryLteAzimuthEvaluationTaskByPage。参数：cond = {},page = {}", cond, page);
        cond.setAccount(account);

        Map<String, Object> result = new HashMap<>();
        List<TaskQueryResult> tasks = rnoLteAzimuthEvaluationService.queryLteAzimuthEvaluationTaskByPage(cond, page);

        long totalCnt = page.getTotalCnt();
        page.setTotalPageCnt(totalCnt / page.getPageSize() + (totalCnt % page.getPageSize() == 0 ? 0 : 1));
        page.setForcedStartIndex(-1);

        result.put("page", page);
        result.put("data", tasks);
        return result;
    }

    /**
     * 查询指定job的报告
     */
    @RequestMapping("/queryJobReportByPage")
    @ResponseBody
    public Map<String, Object> queryJobReportByPage(Long jobId, Page page) {
        logger.debug("queryJobReportByPage.jobId={}", jobId);

        Map<String, Object> result = new HashMap<>();

        if (jobId == null || jobId <= 0) {
            logger.error("未传入一个有效的jobid！无法查看其报告！");
            page.setTotalCnt(0);
            page.setTotalPageCnt(0);
            result.put("page", page);
            result.put("data", Collections.emptyList());
            return result;
        }

        Map<String, Object> map = new HashMap<>();
        int start = (page.getPageSize() * (page.getCurrentPage() - 1) + 1);
        int end = (page.getPageSize() * page.getCurrentPage());
        map.put("jobId", jobId);
        map.put("start", start);
        map.put("end", end);

        List<Report> reportRecs = rnoCommonService.queryJobReportByPage(map);
        logger.debug("reportRecs size:{}", reportRecs == null ? 0 : reportRecs.size());

        if (page.getTotalCnt() < 0) {
            page.setTotalCnt(rnoCommonService.queryJobReportCnt(jobId));
        }
        page.setTotalPageCnt(page.getTotalCnt() / page.getPageSize() + (page.getTotalCnt() % page.getPageSize() == 0 ? 0 : 1));
        page.setForcedStartIndex(-1);

        result.put("page", page);
        result.put("data", reportRecs);
        return result;
    }

    /**
     * 提交LTE 方位角评估分析计算任务
     */
    @RequestMapping("/submitLteAzimuthEvaluationTask")
    public ResponseEntity<String> submitLteAzimuthEvaluationTask(@ModelAttribute("account") String account, AzimuthJobRecord task) {
        logger.info("submitLteAzimuthEvaluationTask， task={}", task);
        try {
            // 保存任务信息
            SubmitResult result = rnoLteAzimuthEvaluationService.submitLteAzimuthEvaluationTask(account, task);

            if (result.isFlag()) {

                // 如果运行模式是定时模式，文件上传成功后就直接结束
                String runMode = rnoProperties.getRunMode();
                logger.debug("rnoProperties.getRunMode() = {}", runMode);

                if (runMode.equals("always")) {
                    logger.debug("运行模式是 always 模式，直接执行任务。");
                    long jobId = result.getJobId();
                    if (jobId > 0) {
                        Thread jobThread = new JobThread(jobId, rnoLteAzimuthEvaluationTask);
                        jobThread.start();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 终止Pci规划任务
     */
    @RequestMapping("/stopJobByJobId")
    @ResponseBody
    public Map<String, Object> stopJobByJobId(Long jobId) {
        logger.debug("进入方法：stopJobByJobId。jobId={}", jobId);

        Map<String, Object> map = new HashMap<>();
        String msg = "任务已停止";
        if (jobId == null || jobId <= 0) {
            msg = "未传入一个有效的jobId！无法停止任务！";
            logger.error(msg);
            map.put("flag", false);
            map.put("result", msg);
            return map;
        }
        rnoCommonService.stopJobByJobId(jobId);
        map.put("flag", true);
        map.put("result", msg);
        return map;
    }

    @RequestMapping("/downloadLteAzimuthFile")
    @ResponseBody
    public ResponseEntity<byte[]> downloadLteAzimuthFile(long jobId) {
        logger.info("下载LTE方位角评估分析结果文件， jobId=" + jobId);

        // 通过 jobId 获取Lte方位角计算记录信息(rno_ms_lte_azimuth_asses_job表），包括变小区的 CLOB 信息
        List<AzimuthJobRecord> records = rnoLteAzimuthEvaluationService.queryLteAzimuthEvaluationJobRecByJobId(jobId);
        logger.debug("records:{}", records);
        String error = "";
        if (records == null || records.isEmpty()) {
            error = "不存在该" + jobId + "任务信息";
            logger.error(error);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        AzimuthJobRecord record = records.get(0);

        if (record.getDlFileName() == null || record.getDlFileName().isEmpty()) {
            error = "不存在该" + jobId + "任务信息";
            logger.error(error);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // 确定临时目录存在
        File tmpDir = new File(System.getProperty("java.io.tmpdir") + "/" + UUID.randomUUID().toString().replaceAll("-", "") + "/");
        if (!tmpDir.exists()) {
            tmpDir.mkdirs();
        }

        String dlFileName = record.getDlFileName();

        File currentFile = new File(tmpDir, dlFileName);
        // 如果本地存在同名文件则删除
        FileUtils.deleteQuietly(currentFile);

        List<AzimuthResult> results = rnoLteAzimuthEvaluationService.queryAzimuthResultsByJobId(record.getJobId());

        //结果为空，无法提供下载
        if (results == null || results.isEmpty()) {
            error = "方位角计算的结果数据不存在！";
            logger.error(error);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        StringBuilder sb = new StringBuilder();
        if ("type1".equalsIgnoreCase(record.getEvalType())) {
            sb.append("城市").append(",").append("小区").append(",").append("原方位角");
            sb.append(",").append("算法一").append(",").append("算法一差值");
            sb.append("\n");
            results.forEach(e -> sb.append(record.getCityName()).append(",")
                    .append(e.getCellId()).append(",")
                    .append(filterNumber(e.getAzimuth())).append(",")
                    .append(filterNumber(e.getAzimuth1())).append(",")
                    .append(filterNumber(e.getDiff1())).append("\n"));
        } else if ("type2".equalsIgnoreCase(record.getEvalType())) {
            sb.append("城市").append(",").append("小区").append(",").append("原方位角");
            sb.append(",").append("算法二").append(",").append("算法二差值");
            sb.append("\n");
            results.forEach(e -> sb.append(record.getCityName()).append(",")
                    .append(e.getCellId()).append(",")
                    .append(filterNumber(e.getAzimuth())).append(",")
                    .append(filterNumber(e.getAzimuth2())).append(",")
                    .append(filterNumber(e.getDiff2())).append("\n"));
        } else {
            sb.append("城市").append(",").append("小区").append(",").append("原方位角");
            sb.append(",").append("算法一").append(",").append("算法一差值");
            sb.append(",").append("算法二").append(",").append("算法二差值");
            sb.append("\n");
            results.forEach(e -> sb.append(record.getCityName()).append(",")
                    .append(e.getCellId()).append(",")
                    .append(filterNumber(e.getAzimuth())).append(",")
                    .append(filterNumber(e.getAzimuth1())).append(",")
                    .append(filterNumber(e.getDiff1())).append(",")
                    .append(filterNumber(e.getAzimuth2())).append(",")
                    .append(filterNumber(e.getDiff2())).append("\n"));
        }

        try (Writer writer = new OutputStreamWriter(new FileOutputStream(currentFile), "GBK")) {
            writer.write(sb.toString());
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //文件不存在，不能提供下载
        if (!currentFile.exists()) {
            error = "方位角计算的结果文件不存在！";
            logger.error(error);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            if (currentFile.exists()) {
                HttpHeaders headers = new HttpHeaders();
                String fileName = new String(dlFileName.getBytes("UTF-8"), "iso-8859-1");// 为了解决中文名称乱码问题
                headers.setContentDispositionFormData("attachment", fileName);
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                return new ResponseEntity<>(FileUtils.readFileToByteArray(currentFile), headers, HttpStatus.CREATED);
            } else {
                logger.error("天线方位角结果文件不存在！");
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(error);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } finally {
            try {
                // 删除临时目录
                FileUtils.deleteDirectory(tmpDir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Object filterNumber(int num) {
        return num == -1 ? "" : num;
    }

    private class JobThread extends Thread {
        private long jobId;
        private RnoLteAzimuthEvaluationTask rnoLteAzimuthEvaluationTask;

        private JobThread(long jobId, RnoLteAzimuthEvaluationTask rnoLteAzimuthEvaluationTask) {
            this.jobId = jobId;
            this.rnoLteAzimuthEvaluationTask = rnoLteAzimuthEvaluationTask;
        }

        @Override
        public void run() {
            rnoLteAzimuthEvaluationTask.runJobInternal(jobId);
        }
    }
}
