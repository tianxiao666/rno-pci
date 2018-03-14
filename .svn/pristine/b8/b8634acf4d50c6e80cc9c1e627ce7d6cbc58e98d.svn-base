package com.hgicreate.rno.web.rest;

import com.hgicreate.rno.model.*;
import com.hgicreate.rno.repo.ReportRepository;
import com.hgicreate.rno.service.AzimuthEvalService;
import com.hgicreate.rno.task.JobExecutor;
import com.hgicreate.rno.web.rest.dto.ReportDTO;
import com.hgicreate.rno.web.rest.dto.TaskQueryResultDTO;
import com.hgicreate.rno.web.rest.vm.AzimuthEvaluationSubmitTaskVm;
import com.hgicreate.rno.web.rest.vm.TaskQueryVm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * 方位角评估API接口
 *
 * @author chen.c10
 * @date 2018-01-12 13:34:00
 */
@Slf4j
@CrossOrigin
@RestController
public class AzimuthEvaluationResource {
    private static final String RUN_MODE_ALWAYS = "always";

    private final AzimuthEvalService azimuthEvalService;
    private final JobExecutor jobExecutor;
    private final ReportRepository reportRepository;

    @Value("${rno.run-mode:always}")
    private String runMode;

    public AzimuthEvaluationResource(AzimuthEvalService azimuthEvalService,
                                     JobExecutor jobExecutor,
                                     ReportRepository reportRepository) {
        this.azimuthEvalService = azimuthEvalService;
        this.jobExecutor = jobExecutor;
        this.reportRepository = reportRepository;
    }

    /**
     * 立即开始任务
     *
     * @param jobId 任务ID
     * @return 开始任务结果
     * @date 2018-01-12 13:34:23
     */
    @GetMapping("/start-task/{jobId}")
    public SubmitResult startTask(@PathVariable("jobId") long jobId) {
        log.debug("进入方法:startTask.jobId={}", jobId);
        String msg;
        SubmitResult submitResult = new SubmitResult();
        submitResult.setFlag(false);
        try {
            // 如果运行模式是定时模式，文件上传成功后就直接结束
            if (Objects.equals(RUN_MODE_ALWAYS, runMode)) {
                log.debug("运行模式是 always 模式，直接执行任务。jobId={}", jobId);
                Job job = azimuthEvalService.getJobByJobId(jobId);
                if (null != job && job.getJobId() > 0) {
                    new Thread(() -> jobExecutor.runJobInternal(job.getJobId())).start();
                    submitResult.setFlag(true);
                    msg = "任务已经启动，请耐心等待。。。";
                    log.debug(msg);
                } else {
                    msg = "任务信息有误，无法启动任务。";
                    log.error(msg);
                }
            } else {
                submitResult.setFlag(true);
                msg = "任务非及时模式，请耐心等待。。。";
                log.debug(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
            msg = "启动任务失败！";
            log.error(msg);
        }
        submitResult.setResult(msg);
        log.debug("退出方法：submitTask。submitResult={}", submitResult);
        return submitResult;
    }

    /**
     * 下载结果文件
     *
     * @param jobId 任务ID
     * @return 方位角评估结果对象
     * @date 2018-01-12 13:35:03
     */
    @RequestMapping("/download-result/{jobId}")
    @ResponseBody
    public FileResult downloadResultFile(@PathVariable("jobId") long jobId) {
        log.info("进入方法：downloadResultFile，下载LTE方位角评估结果文件， jobId=" + jobId);

        String msg = "未知错误";
        FileResult fileResult = new FileResult();
        fileResult.setResult(false);
        fileResult.setStatusCode(HttpStatus.NOT_FOUND.value());

        if (jobId <= 0) {
            msg = "任务ID不存在";
            log.error(msg);
            fileResult.setMsg(msg);
            return fileResult;
        }

        // 确定临时目录存在
        try {
            // 确定临时目录存在
            Path downloadPath = Paths.get(System.getProperty("java.io.tmpdir"), "download");
            Files.createDirectories(downloadPath);
            Path tmpDir = Files.createTempDirectory(downloadPath, "azimuth_");

            AzimuthEvalTask record = azimuthEvalService.queryTaskRecordByJobId(jobId);
            log.debug("LTE天线方位角评估的数据信息：" + record);
            if (record == null) {
                msg = "不存在该" + jobId + "任务信息";
                log.error(msg);
                fileResult.setMsg(msg);
                return fileResult;
            }

            String dlFileName = record.getDlFileName();
            Path currentFile = Paths.get(tmpDir.toString(), dlFileName);

            // 获取结果
            List<Map<String, Object>> results = azimuthEvalService.queryResultByJobId(record.getJobId());

            //结果为空，无法提供下载
            if (results == null || results.isEmpty()) {
                msg = "方位角计算的结果数据不存在！";
                log.error(msg);
                fileResult.setMsg(msg);
                return fileResult;
            }

            StringBuilder sb = new StringBuilder();

            sb.append("城市").append(",").append("小区ID").append(",").append("小区名")
                    .append(",").append("原方位角");
            sb.append(",").append("新方位角").append(",").append("差值");
            sb.append("\n");
            results.forEach(e -> sb.append(record.getArea().getName()).append(",")
                    .append(e.get("cell_id")).append(",")
                    .append(e.get("cell_name")).append(",")
                    .append(filterNumber(Integer.parseInt(e.get("azimuth").toString()))).append(",")
                    .append(filterNumber(Integer.parseInt(e.get("azimuth1").toString()))).append(",")
                    .append(filterNumber(Integer.parseInt(e.get("diff").toString()))).append("\n"));

            Files.copy(new ByteArrayInputStream(sb.toString().getBytes("GBK")), currentFile, StandardCopyOption.REPLACE_EXISTING);

            if (Files.exists(currentFile)) {
                msg = "文件下载成功。";
                log.debug(msg);
                fileResult.setResult(true);
                fileResult.setMsg(msg);
                fileResult.setStatusCode(HttpStatus.CREATED.value());
                fileResult.setFilename(currentFile.getFileName().toString());

                byte[] fileBody = Files.readAllBytes(currentFile);
                fileResult.setFileBody(fileBody);
                fileResult.setFileLength(fileBody.length);
                return fileResult;
            } else {
                //文件不存在，不能提供下载
                msg = "天线方位角结果文件不存在！";
                log.error(msg);
                fileResult.setMsg(msg);
                return fileResult;
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(msg);
            fileResult.setMsg(msg);
            return fileResult;
        }
    }

    private Object filterNumber(int num) {
        return num == -1 ? "" : num;
    }

    /**
     * 下载增强方案文件
     *
     * @param jobId 任务ID
     * @return 增强方案结果对象
     * @date 2018-01-12 13:38:46
     */
    @RequestMapping("/download-enhance/{jobId}")
    @ResponseBody
    public FileResult downloadEnhanceFile(@PathVariable("jobId") long jobId) {
        String msg = "未知错误";
        FileResult fileResult = new FileResult();
        fileResult.setResult(false);
        fileResult.setStatusCode(HttpStatus.NOT_FOUND.value());

        if (jobId <= 0) {
            msg = "任务ID不存在";
            log.error(msg);
            fileResult.setMsg(msg);
            return fileResult;
        }

        // 确定临时目录存在
        Path downloadPath = Paths.get(System.getProperty("java.io.tmpdir"), "download");
        try {
            Files.createDirectories(downloadPath);
            Path tmpDir = Files.createTempDirectory(downloadPath, "azimuth_");

            AzimuthEvalTask record = azimuthEvalService.queryTaskRecordByJobId(jobId);
            log.debug("LTE天线方位角评估的数据信息：" + record);

            if (record == null) {
                msg = "不存在该" + jobId + "任务信息";
                log.error(msg);
                fileResult.setMsg(msg);
                return fileResult;
            }

            String dlFileName = record.getDlFileName();
            Path currentFile = Paths.get(tmpDir.toString(), dlFileName);

            // 获取结果
            List<Map<String, Object>> results = azimuthEvalService.queryEnhanceByJobId(record.getJobId());

            //结果为空，无法提供下载
            if (results == null || results.isEmpty()) {
                msg = "方位角计算的结果数据不存在！";
                log.error(msg);
                fileResult.setMsg(msg);
                return fileResult;
            }

            StringBuffer sb = new StringBuffer();

            sb.append("城市").append(",").append("小区ID").append(",").append("小区名").append(",")
                    .append("\"网络覆盖增强方案\r\n（天线方位角调整至）\"").append("\n");
            results.forEach(e -> sb.append(record.getArea().getName()).append(",")
                    .append(e.get("cell_id")).append(",")
                    .append(e.get("cell_name")).append(",")
                    .append(e.get("result")).append("\n"));
            Files.copy(new ByteArrayInputStream(sb.toString().getBytes("GBK")), currentFile, StandardCopyOption.REPLACE_EXISTING);

            if (Files.exists(currentFile)) {
                msg = "文件下载成功。";
                log.debug(msg);
                fileResult.setResult(true);
                fileResult.setMsg(msg);
                fileResult.setStatusCode(HttpStatus.CREATED.value());
                fileResult.setFilename(record.getArea().getName() + "_" + record.getJobId() + "_LTE网络覆盖增强方案.csv");

                byte[] fileBody = Files.readAllBytes(currentFile);
                fileResult.setFileBody(fileBody);
                fileResult.setFileLength(fileBody.length);
                return fileResult;
            } else {
                //文件不存在，不能提供下载
                msg = "天线方位角结果文件不存在！";
                log.error(msg);
                fileResult.setMsg(msg);
                return fileResult;
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(msg);
            fileResult.setMsg(msg);
            return fileResult;
        }
    }

    /**
     * 通过jobId获取报告
     *
     * @param jobId 任务ID
     * @return 报告结果集
     * @date 2018-01-12 13:39:33
     */
    @GetMapping("/query-report/{jobId}")
    public List<ReportDTO> getReportsByJobId(@PathVariable("jobId") long jobId) {
        log.debug("进入方法：getReportsByJobId。jobId={}", jobId);
        return reportRepository.findByJobId(jobId).stream().map(ReportDTO::new).collect(Collectors.toList());
    }

    /**
     * 查询数据记录
     *
     * @param cond 查询条件
     * @return 任务结果集
     * @date 2018-01-12 13:40:22
     */
    @PostMapping("/query-task")
    public List<TaskQueryResultDTO> queryTaskByPage(@RequestBody TaskQueryVm cond) {
        log.debug("进入方法:queryTaskByPage。cond = {}", cond);
        List<AzimuthEvalTask> res = azimuthEvalService.findAllTask(cond);
        return res.stream().map(TaskQueryResultDTO::new).collect(Collectors.toList());
    }

    /**
     * 通过 jobId 获取Lte方位角计算记录信息
     *
     * @param jobId 任务ID
     * @return 相应任务信息
     * @date 2018-01-12 13:41:45
     */
    @GetMapping("/query-task/{jobId}")
    public AzimuthEvalTask queryTaskRecordByJobId(@PathVariable("jobId") long jobId) {
        log.debug("进入方法：queryTaskRecordByJobId。jobId={}", jobId);
        return azimuthEvalService.getOneAzimuthEvalTask(jobId);
    }

    /**
     * 提交LTE 方位角评估分析计算任务
     *
     * @param cond 查询条件
     * @return 提交任务结果
     * @date 2018-01-12 13:43:45
     */
    @PostMapping("/submit-task")
    public SubmitResult submitTask(@RequestBody AzimuthEvaluationSubmitTaskVm cond) {
        log.debug("进入方法:submitTask.cond={}", cond);
        SubmitResult submitResult = azimuthEvalService.submitTask(cond);
        log.debug("离开方法：submitTask。submitResult={}", submitResult);
        return submitResult;
    }

    /**
     * 更新task状态
     *
     * @param jobId     任务ID
     * @param jobStatus 任务状态
     * @return 更新结果
     * @date 2018-01-12 13:44:22
     */
    @RequestMapping(value = "/updateTaskStatus/{jobId}", method = POST)
    public boolean updateTaskStatus(@PathVariable("jobId") long jobId, String jobStatus) {
        log.debug("进入方法：updateTaskStatus。jobId={},jobStatus={}", jobId, jobStatus);
        return azimuthEvalService.updateTaskStatus(jobId, jobStatus);
    }

    /**
     * 更新task测量时间
     *
     * @param jobId 任务ID
     * @param date  测量时间
     * @return 更新结果
     * @date 2018-01-12 13:45:45
     */
    @RequestMapping(value = "/update-meatime/{jobId}/{date}", method = POST)
    public boolean updateTaskMeaTime(@PathVariable("jobId") long jobId, @PathVariable("date") String date) {
        log.debug("进入方法：updateTaskMeaTime。jobId={}, date={}", jobId, date);
        return azimuthEvalService.updateTaskMeaTime(jobId, date);
    }

    /**
     * 根据任务ID获取计算结果
     *
     * @param jobId 任务ID
     * @return 方位角评估结果集
     * @date 2018-01-12 13:46:33
     */
    @Cacheable("azimuthEvalResult")
    @GetMapping("/query-result/{jobId}")
    public List queryResultByJobId(@PathVariable("jobId") long jobId) {
        log.debug("进入方法:queryResultByJobId.jobId={}", jobId);
        return azimuthEvalService.findAzimuthEvalResultsByJobId(jobId);
    }

    /**
     * 根据任务ID获取1000计算结果
     *
     * @param jobId 任务ID
     * @return 1000条方位角评估结果
     * @date 2018-01-12 13:47:35
     */
    @Cacheable("azimuthEvalTop1000Result")
    @GetMapping("/query-1000-result/{jobId}")
    public List queryTop1000ResultByJobId(@PathVariable("jobId") long jobId) {
        log.debug("进入方法:queryTop1000ResultByJobId.jobId={}", jobId);
        return azimuthEvalService.findTop1000AzimuthEvalResultsByJobId(jobId);
    }

    /**
     * 根据任务ID获取网络覆盖增强方案结果
     *
     * @param jobId 任务ID
     * @return 增强方案结果集
     * @date 2018-01-12 13:47:33
     */
    @Cacheable("azimuthEvalEnhancePlan")
    @GetMapping("/query-enhance/{jobId}")
    public List queryEnhanceByJobId(@PathVariable("jobId") long jobId) {
        log.debug("进入方法:queryEnhanceByJobId.jobId={}", jobId);
        List<Map<String, Object>> res = azimuthEvalService.findAzimuthEvalResultsByJobId(jobId);
        res.forEach(map -> map.put("result", new Random().nextInt(360) - 180));
        return res;
    }

    /**
     * 根据任务ID获取1000网络覆盖增强方案结果
     *
     * @param jobId 任务ID
     * @return 1000条增强方案结果
     * @date 2018-01-12 13:48:48
     */
    @Cacheable("azimuthEvalTop1000EnhancePlan")
    @GetMapping("/query-1000-enhance/{jobId}")
    public List queryTop1000EnhanceByJobId(@PathVariable("jobId") long jobId) {
        log.debug("进入方法:queryTop1000EnhanceByJobId.jobId={}", jobId);
        List<Map<String, Object>> res = azimuthEvalService.findTop1000AzimuthEvalResultsByJobId(jobId);
        res.forEach(map -> map.put("result", new Random().nextInt(360) - 180));
        return res;
    }

    /**
     * 保存天线方位角结果
     *
     * @param azimuthEvalResults 方位角评估结果
     * @return 保存结果
     * @date 2018-01-12: 13:49:34
     */
    @PostMapping("/saveResult")
    public long saveResult(@RequestBody List<AzimuthEvalResult> azimuthEvalResults) {
        log.debug("进入方法:saveResult.size={}", azimuthEvalResults.size());
        List<AzimuthEvalResult> results = azimuthEvalService.saveAzimuthEvalResults(azimuthEvalResults);
        return results.size();
    }
}
