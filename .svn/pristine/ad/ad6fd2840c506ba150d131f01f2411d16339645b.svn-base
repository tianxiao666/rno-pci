package com.hgicreate.rno.ltestrucanlsclient.controller;

import com.hgicreate.rno.ltestrucanlsclient.client.RnoCommonRestClient;
import com.hgicreate.rno.ltestrucanlsclient.client.RnoLteStrucAnlsRestClient;
import com.hgicreate.rno.ltestrucanlsclient.model.*;
import com.hgicreate.rno.ltestrucanlsclient.tool.ZipFileHandler;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

@Controller
@SessionAttributes("account")
public class RnoLteStrucAnlsClientController {
    private static final Logger logger = LoggerFactory.getLogger(RnoLteStrucAnlsClientController.class);

//    private String account = "liu.yp@iscreate.com";

    private RnoCommonRestClient rnoCommonRestClient;

    private RnoLteStrucAnlsRestClient rnoLteStrucAnlsRestClient;

    public RnoLteStrucAnlsClientController(RnoCommonRestClient rnoCommonRestClient, RnoLteStrucAnlsRestClient rnoLteStrucAnlsRestClient) {
        this.rnoCommonRestClient = rnoCommonRestClient;
        this.rnoLteStrucAnlsRestClient = rnoLteStrucAnlsRestClient;
    }

    /**
     * 通过账户和城市Id初始化用户区域信息
     */
    private Map<String, Object> initAreaByAccountAndDefaultCityId(String account, long currentCityId) {
        Map<String, Object> model = new HashMap<>();
        try {
            Map<String, List<Area>> map = rnoCommonRestClient.getAreaByAccount(account, currentCityId);
            model.put("provinces", map.get("provinceAreas"));
            model.put("cities", map.get("cityAreas"));
        } catch (Exception e) {
            logger.error("获取用户区域失败！！");
        }
        return model;
    }

    /**
     * 访问首页
     */
    @RequestMapping("/")
    public String indexGet(Map<String, Object> model, String account) {
        logger.debug("访问首页。account={}", account);

        if (!rnoCommonRestClient.verifyUserIdentity(account)) {
            model.put("error", "非法登录用户，请选择正常渠道登录，谢谢！");
            return "rno_fail";
        }

        Map<String, List<Area>> map = rnoCommonRestClient.getAreaByAccount(account, -1);
        List<Area> provinceAreas = map.get("provinceAreas");
        if (null == provinceAreas || provinceAreas.isEmpty()) {
            model.put("error", "当前用户没有足够的权限！");
            return "rno_fail";
        }
        model.put("provinces", provinceAreas);
        model.put("cities", map.get("cityAreas"));

        model.put("account", account);
        return "rno_lte_structure_analysis";
    }

    /**
     * 返回首页
     */
    @PostMapping("/backIndex")
    public String indexPost(Map<String, Object> model, @ModelAttribute("account") String account, @RequestParam(defaultValue = "-1") long currentCityId) {
        logger.debug("访问首页。account={},currentCityId={}", account, currentCityId);
        model.putAll(initAreaByAccountAndDefaultCityId(account, currentCityId));
        return "rno_lte_structure_analysis";
    }

    /**
     * 新建任务页面
     */
    @RequestMapping("/createTask")
    public String createTask(Map<String, Object> model, @ModelAttribute("account") String account, @RequestParam(defaultValue = "-1") long currentCityId) {
        logger.debug("进入新建任务页。currentCityId={}", currentCityId);
        model.putAll(initAreaByAccountAndDefaultCityId(account, currentCityId));
        return "rno_lte_structure_analysis_task";
    }

    /**
     * 根据父区域ID，获取指定类型的子区域列表
     */
    @RequestMapping("/getSubAreaByParent")
    @ResponseBody
    public List<Area> getSubAreaByParent(@ModelAttribute("account") String account, long parentAreaId, String subAreaLevel) {
        logger.debug("进入getSubAreaByParent方法。parentAreaId={},subAreaLevel={}", parentAreaId, subAreaLevel);
        return rnoCommonRestClient.getSubAreaByParent(account, parentAreaId, subAreaLevel);
    }

    /**
     * 分页查询LTE结构分析计算任务信息
     */
    @RequestMapping("/queryLteStrucAnlsTaskByPage")
    @ResponseBody
    public Map<String, Object> queryLteStrucAnlsTaskByPage(@ModelAttribute("account") String account, TaskQueryCond cond, Page page) {
        logger.debug("queryLteStrucAnlsTaskByPage。cond = {},page = {}", cond, page);
        cond.setAccount(account);
        Map<String, Object> result = new HashMap<>();
        cond.setStart(page.getPageSize() * (page.getCurrentPage() - 1) + 1);
        cond.setEnd(page.getPageSize() * page.getCurrentPage());

        MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
        param.add("account", cond.getAccount());
        param.add("cityId", cond.getCityId());
        param.add("isMine", cond.getIsMine());
        param.add("taskName", cond.getTaskName());
        param.add("taskStatus", cond.getTaskStatus());
        param.add("meaTime", cond.getMeaTime());
        param.add("startSubmitTime", cond.getStartSubmitTime());
        param.add("endSubmitTime", cond.getEndSubmitTime());
        param.add("start", cond.getStart());
        param.add("end", cond.getEnd());

        List<TaskQueryResult> taskQueryResults = rnoLteStrucAnlsRestClient.queryLteStrucAnlsTaskByPage(param);

        if (page.getTotalCnt() < 0) {
            page.setTotalCnt(rnoLteStrucAnlsRestClient.queryLteStrucAnlsTaskCnt(param));
        }
        page.setTotalPageCnt(page.getTotalCnt() / page.getPageSize() + (page.getTotalCnt() % page.getPageSize() == 0 ? 0 : 1));
        page.setForcedStartIndex(-1);

        result.put("page", page);
        result.put("data", taskQueryResults);
        return result;
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
        rnoCommonRestClient.stopJobByJobId(jobId);
        map.put("flag", true);
        map.put("result", msg);
        return map;
    }

    /**
     * 查询指定job的报告
     */
    @RequestMapping("/queryReport")
    @ResponseBody
    public Map<String, Object> queryReport(Long jobId, Page page) {
        logger.debug("queryReport.jobId={}", jobId);

        Map<String, Object> result = new HashMap<>();

        if (jobId == null || jobId <= 0) {
            logger.error("未传入一个有效的jobId！无法查看其报告！");
            page.setTotalCnt(0);
            page.setTotalPageCnt(0);
            result.put("page", page);
            result.put("data", Collections.emptyList());
            logger.debug("退出queryReport。输出：{}", result);
            return result;
        }

        int start = (page.getPageSize() * (page.getCurrentPage() - 1) + 1);
        int end = (page.getPageSize() * page.getCurrentPage());

        List<Report> reportRecs = rnoCommonRestClient.queryReportByPage(jobId, start, end);
        logger.debug("reportRecs size:{}", reportRecs == null ? 0 : reportRecs.size());

        if (page.getTotalCnt() < 0) {
            page.setTotalCnt(rnoCommonRestClient.queryReportCnt(jobId));
        }
        page.setTotalPageCnt(page.getTotalCnt() / page.getPageSize() + (page.getTotalCnt() % page.getPageSize() == 0 ? 0 : 1));
        page.setForcedStartIndex(-1);

        result.put("page", page);
        result.put("data", reportRecs);
        return result;
    }

    @RequestMapping("/submitLteStrucAnlsTask")
    @ResponseBody
    public Map<String, Object> submitLteStrucAnlsTask(@ModelAttribute("account") String account, SubmitTaskCond cond) {
        logger.debug("进入submitLteStrucAnlsTask.cond={}", cond);

        // 用户名
        cond.setAccount(account);

        Map<String, Object> map = new HashMap<>();
        String msg;
        // 执行
        try {
            // 创建job
            JobProfile job = new JobProfile();
            job.setAccount(cond.getAccount());
            job.setJobName(cond.getTaskName());
            job.setJobType("RNO_LTE_STRUC_ANLS");
            job.setDescription(cond.getTaskDesc());

            Long jobId = rnoCommonRestClient.addOneJob(job);
            if (jobId == null || jobId == 0) {
                msg = "创建jobId失败！";
                logger.error(msg);
                map.put("flag", false);
                map.put("result", msg);
                return map;
            }

            cond.setJobId(jobId);
            cond.setDlFileName(cond.getCityName() + "_" + jobId + "_LTE结构优化分析结果.zip");

            if (rnoLteStrucAnlsRestClient.addLteStrucAnlsTask(cond)) {
                map.put("flag", true);
                map.put("result", "任务提交成功，请等待分析完成！");
                return map;
            } else {
                msg = "创建结构优化任务失败！";
                logger.error(msg);
                map.put("flag", false);
                map.put("result", msg);
                return map;
            }
        } catch (Exception e) {
            e.printStackTrace();
            msg = "创建结构优化任务失败！";
            logger.error(msg);
            map.put("flag", false);
            map.put("result", msg);
            return map;
        }
    }

    @RequestMapping("/downloadLteStructureFile")
    public ResponseEntity<byte[]> downloadLteStructureFile(Long jobId) {
        logger.info("下载LTE方位角评估分析结果文件， jobId=" + jobId);

        String error = "未知错误";

        HttpHeaders errorHeaders = new HttpHeaders();
        // 缺省返回json类型错误信息
        errorHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);

        if (jobId == null || jobId == 0) {
            error = "任务ID不存在";
            logger.error(error);
            // 用户请求了无效的任务ID，返回Bad Request
            return new ResponseEntity<>(error.getBytes(), errorHeaders, HttpStatus.BAD_REQUEST);
        }

        // 确定临时目录存在
        File tmpDir = new File(System.getProperty("java.io.tmpdir"), UUID.randomUUID().toString().replaceAll("-", ""));
        if (!tmpDir.exists()) {
            tmpDir.mkdirs();
        }

        try {
            StrucTaskRecord record = rnoLteStrucAnlsRestClient.queryLteStrucAnlsTaskRecByJobId(jobId);
            logger.debug("LTE结构优化分析的数据信息：" + record);
            if (record == null) {
                error = "不存在该" + jobId + "任务信息";
                logger.error(error);
                // 任务信息未找到，返回Not Found
                return new ResponseEntity<>(error.getBytes(), errorHeaders, HttpStatus.NOT_FOUND);
            }

            // 指标文件集合
            List<File> files = new ArrayList<>();

            // 重叠覆盖部分
            // 获取结果
            List<OverlapCover> overlapCovers = rnoLteStrucAnlsRestClient.queryOverlapCoverResultByJobId(jobId);

            //结果为空，无法提供下载
            if (!overlapCovers.isEmpty()) {
                // 生成临时文件
                File overlapCoverFile = new File(tmpDir, record.getCityName() + "_重叠覆盖.csv");
                // 如果本地存在同名文件则删除
                FileUtils.deleteQuietly(overlapCoverFile);
                // 生成文件
                try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(overlapCoverFile), "GBK"))) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("城市").append(",").append("时间").append(",").append("DATATYPE").append(",").append("厂家").append(",").append("CELLID").append(",").append("日期").append(",").append("HOURS")
                            .append(",").append("总采样点数").append(",").append("大于负105dBm的采样点数").append(",").append("大于负110dBm的采样点数").append(",").append("覆盖率大于负105dBm")
                            .append(",").append("覆盖率大于负110dBm").append(",").append("db6内3邻区采样数").append(",").append("db6内4邻区采样数").append(",").append("db6内5邻区采样数")
                            .append(",").append("db6内6邻区采样数").append(",").append("db10内3邻区采样数").append(",").append("db10内4邻区采样数").append(",").append("db10内5邻区采样数")
                            .append(",").append("db10内6邻区采样数").append(",").append("db6内3邻区采样比例").append(",").append("db6内4邻区采样比例").append(",").append("db6内5邻区采样比例")
                            .append(",").append("db6内6邻区采样比例").append(",").append("db10内3邻区采样比例").append(",").append("db10内4邻区采样比例").append(",").append("db10内5邻区采样比例")
                            .append(",").append("db10内6邻区采样比例").append(",").append("无邻区采样点数").append(",").append("邻区1个及以上采样数").append(",").append("邻区2个及以上采样数")
                            .append(",").append("邻区3个及以上采样数").append(",").append("邻区4个及以上采样数").append(",").append("邻区5个及以上采样数").append(",").append("邻区6个及以上采样数")
                            .append(",").append("邻区7个及以上采样数").append(",").append("邻区8个及以上采样数").append(",").append("邻区9个及以上采样数");
                    writer.write(sb.toString());
                    writer.newLine();
                    overlapCovers.forEach(e -> {
                                sb.setLength(0);
                                sb.append(record.getCityName()).append(",").append("").append(",").append("仅同频").append(",").append("").append(",")
                                        .append(e.getCellId()).append(",").append("").append(",").append("").append(",")
                                        .append(filterNumber(e.getTotalCnt())).append(",")
                                        .append(filterNumber(e.getScGt105Gt0Cnt())).append(",")
                                        .append(filterNumber(e.getScGt110Gt0Cnt())).append(",")
                                        .append(filterNumber(e.getScGt105Gt0Per())).append(",")
                                        .append(filterNumber(e.getScGt110Gt0Per())).append(",")
                                        .append(filterNumber(e.getScGt110NcScGt6Gt3Cnt())).append(",")
                                        .append(filterNumber(e.getScGt110NcScGt6Gt4Cnt())).append(",")
                                        .append(filterNumber(e.getScGt110NcScGt6Gt5Cnt())).append(",")
                                        .append(filterNumber(e.getScGt110NcScGt6Gt6Cnt())).append(",")
                                        .append(filterNumber(e.getScGt110NcScGt10Gt3Cnt())).append(",")
                                        .append(filterNumber(e.getScGt110NcScGt10Gt4Cnt())).append(",")
                                        .append(filterNumber(e.getScGt110NcScGt10Gt5Cnt())).append(",")
                                        .append(filterNumber(e.getScGt110NcScGt10Gt6Cnt())).append(",")
                                        .append(filterNumber(e.getScGt110NcScGt6Gt3Per())).append(",")
                                        .append(filterNumber(e.getScGt110NcScGt6Gt4Per())).append(",")
                                        .append(filterNumber(e.getScGt110NcScGt6Gt5Per())).append(",")
                                        .append(filterNumber(e.getScGt110NcScGt6Gt6Per())).append(",")
                                        .append(filterNumber(e.getScGt110NcScGt10Gt3Per())).append(",")
                                        .append(filterNumber(e.getScGt110NcScGt10Gt4Per())).append(",")
                                        .append(filterNumber(e.getScGt110NcScGt10Gt5Per())).append(",")
                                        .append(filterNumber(e.getScGt110NcScGt10Gt6Per())).append(",")
                                        .append(0).append(",")
                                        .append(filterNumber(e.getScGt110Eq1Cnt())).append(",")
                                        .append(filterNumber(e.getScGt110Eq2Cnt())).append(",")
                                        .append(filterNumber(e.getScGt110Eq3Cnt())).append(",")
                                        .append(filterNumber(e.getScGt110Eq4Cnt())).append(",")
                                        .append(filterNumber(e.getScGt110Eq5Cnt())).append(",")
                                        .append(filterNumber(e.getScGt110Eq6Cnt())).append(",")
                                        .append(filterNumber(e.getScGt110Eq7Cnt())).append(",")
                                        .append(filterNumber(e.getScGt110Eq8Cnt())).append(",")
                                        .append(filterNumber(e.getScGt110Eq9Cnt())).append(",");
                                try {
                                    writer.write(sb.toString());
                                    writer.newLine();
                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                }
                            }
                    );
                    writer.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //文件不存在，不能提供下载
                if (!overlapCoverFile.exists()) {
                    error = "重叠覆盖的结果文件不存在！";
                    logger.error(error);
                } else {
                    files.add(overlapCoverFile);
                }
            } else {
                error = jobId + " 重叠覆盖的结果数据不存在！";
                logger.error(error);
            }

            // 过覆盖部分
            // 获取结果
            List<OverCover> overCovers = rnoLteStrucAnlsRestClient.queryOverCoverResultByJobId(jobId);

            //结果为空，无法提供下载
            if (!overCovers.isEmpty()) {
                // 生成临时文件
                File overCoverFile = new File(tmpDir, record.getCityName() + "_过覆盖.csv");
                // 如果本地存在同名文件则删除
                FileUtils.deleteQuietly(overCoverFile);
                // 生成文件
                try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(overCoverFile), "GBK"))) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("城市").append(",").append("CELLID").append(",").append("小区名")
                            .append(",").append("小区PCI").append(",").append("小区频点")
                            .append(",").append("小区经度").append(",").append("小区纬度")
                            .append(",").append("邻区ID").append(",").append("邻区名")
                            .append(",").append("邻区PCI").append(",").append("邻区频点")
                            .append(",").append("邻区经度").append(",").append("邻区纬度")
                            .append(",").append("总采样点数").append(",").append("邻区采样点数").append(",").append("采样比例")
                            .append(",").append("距离").append(",").append("理想覆盖距离");
                    writer.write(sb.toString());
                    writer.newLine();
                    overCovers.forEach(e -> {
                                sb.setLength(0);
                                sb.append(record.getCityName()).append(",")
                                        .append(e.getCellId()).append(",")
                                        .append(e.getCellName()).append(",")
                                        .append(e.getCellPci()).append(",")
                                        .append(e.getCellEarfcn()).append(",")
                                        .append(e.getCellLon()).append(",")
                                        .append(e.getCellLat()).append(",")
                                        .append(e.getNcellId()).append(",")
                                        .append(e.getNcellName()).append(",")
                                        .append(e.getNcellPci()).append(",")
                                        .append(e.getNcellEarfcn()).append(",")
                                        .append(e.getNcellLon()).append(",")
                                        .append(e.getNcellLat()).append(",")
                                        .append(e.getTotalCnt()).append(",")
                                        .append(e.getNcellCnt()).append(",")
                                        .append(e.getNcellPer()).append(",")
                                        .append(e.getDis()).append(",")
                                        .append(e.getStationSpace());
                                try {
                                    writer.write(sb.toString());
                                    writer.newLine();
                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                }
                            }
                    );
                    writer.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //文件不存在，不能提供下载
                if (!overCoverFile.exists()) {
                    error = "过覆盖的结果文件不存在！";
                    logger.error(error);
                } else {
                    files.add(overCoverFile);
                }
            } else {
                error = jobId + " 过覆盖的结果数据不存在！";
                logger.error(error);
            }

            // 指标汇总部分
            // 获取结果
            List<MetricsSummary> metricsSummaries = rnoLteStrucAnlsRestClient.queryMetricsSummaryResultByJobId(jobId);

            //结果为空，无法提供下载
            if (!metricsSummaries.isEmpty()) {
                // 生成临时文件
                File metricsSummaryFile = new File(tmpDir, record.getCityName() + "_指标汇总.csv");
                // 如果本地存在同名文件则删除
                FileUtils.deleteQuietly(metricsSummaryFile);
                // 生成文件
                try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(metricsSummaryFile), "GBK"))) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("城市").append(",").append("CELLID").append(",").append("小区名")
                            .append(",").append("弱覆盖").append(",").append("重叠覆盖")
                            .append(",").append("过覆盖").append(",").append("过覆盖数")
                            .append(",").append("1.6过覆盖").append(",").append("1.6过覆盖数");
                    writer.write(sb.toString());
                    writer.newLine();
                    metricsSummaries.forEach(e -> {
                                sb.setLength(0);
                                sb.append(record.getCityName()).append(",")
                                        .append(e.getCellId()).append(",")
                                        .append(e.getCellName()).append(",")
                                        .append(e.getWeakFlag()).append(",")
                                        .append(e.getOverlapFlag()).append(",")
                                        .append(e.getOverFlag()).append(",")
                                        .append(e.getOverCnt()).append(",")
                                        .append(e.getOver16Flag()).append(",")
                                        .append(e.getOver16Cnt());
                                try {
                                    writer.write(sb.toString());
                                    writer.newLine();
                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                }
                            }
                    );
                    writer.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //文件不存在，不能提供下载
                if (!metricsSummaryFile.exists()) {
                    error = "指标汇总的结果文件不存在！";
                    logger.error(error);
                } else {
                    files.add(metricsSummaryFile);
                }
            } else {
                error = jobId + " 指标汇总的结果数据不存在！";
                logger.error(error);
            }

            // 压缩文件
            File currentFile = new File(tmpDir, record.getDlFileName());
            // 如果本地存在同名文件则删除
            FileUtils.deleteQuietly(currentFile);
            // 生成压缩文件
            ZipFileHandler.zip(currentFile, files.toArray(new File[files.size()]));

            if (currentFile.exists()) {
                byte[] fileBody = FileUtils.readFileToByteArray(currentFile);

                HttpHeaders fileHeaders = new HttpHeaders();
                fileHeaders.setContentDispositionFormData("attachment", record.getDlFileName(), Charset.forName("UTF-8"));
                fileHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                fileHeaders.setContentLength(fileBody.length);

                return new ResponseEntity<>(fileBody, fileHeaders, HttpStatus.CREATED);
            } else {
                //文件不存在，不能提供下载
                error = "结构优化分析的结果文件不存在！";
                logger.error(error);
                // 用户请求的结果文件无法生成，返回 Not Acceptable
                return new ResponseEntity<>(error.getBytes(), errorHeaders, HttpStatus.NOT_ACCEPTABLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(error);
            // 其他错误返回 Not Found
            return new ResponseEntity<>(error.getBytes(), errorHeaders, HttpStatus.NOT_FOUND);
        } finally {
            try {
                // 删除临时目录
                FileUtils.deleteDirectory(tmpDir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Object filterNumber(Object num) {
        return Objects.equals(num, -1) ? "" : num;
    }
}
