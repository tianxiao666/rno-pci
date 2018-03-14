package com.hgicreate.rno.lte.pciafp.controller;

import com.hgicreate.rno.lte.pciafp.model.*;
import com.hgicreate.rno.lte.pciafp.service.CommonRestService;
import com.hgicreate.rno.lte.pciafp.service.PciAfpDataService;
import com.hgicreate.rno.lte.pciafp.service.PciAfpRestService;
import com.hgicreate.rno.lte.pciafp.task.JobExecutor;
import com.hgicreate.rno.lte.pciafp.tool.FileTool;
import com.hgicreate.rno.lte.pciafp.tool.ZipFileHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * @author chen.c10
 */
@Slf4j
@RestController
public class PciAfpServiceController {

    private final CommonRestService commonRestService;
    private final PciAfpRestService pciAfpRestService;
    private final PciAfpDataService pciAfpDataService;
    private final JobExecutor jobExecutor;
    @Value("${rno.scheduler.run-mode:always}")
    private String runMode;
    @Value("${rno.job-type-code:RNO_PCI_AFP_PLAN}")
    private String jobTypeCode;

    public PciAfpServiceController(CommonRestService commonRestService, PciAfpRestService pciAfpRestService, PciAfpDataService pciAfpDataService, JobExecutor jobExecutor) {
        this.commonRestService = commonRestService;
        this.pciAfpRestService = pciAfpRestService;
        this.pciAfpDataService = pciAfpDataService;
        this.jobExecutor = jobExecutor;
    }

    /**
     * 立即开始任务
     */
    @GetMapping("/startTask/{jobId}")
    public SubmitResult startTask(@PathVariable("jobId") long jobId) {
        log.debug("进入方法:startTask.jobId={}", jobId);
        String msg;
        SubmitResult submitResult = new SubmitResult();
        submitResult.setFlag(false);
        try {
            // 如果运行模式是定时模式，文件上传成功后就直接结束
            if ("always".equals(runMode)) {
                log.debug("运行模式是 always 模式，直接执行任务。jobId={}", jobId);
                Job job = commonRestService.getJobByJobId(jobId);
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
     * 下载PCI结果文件
     */
    @RequestMapping(value = "/downloadResultFile/{jobId}", method = GET)
    public FileResult downloadResultFile(@PathVariable("jobId") long jobId) {
        log.debug("进入方法：downloadResultFile。下载PCI规划结果文件， jobId={}", jobId);

        String msg;
        FileResult fileResult = new FileResult();
        fileResult.setResult(false);
        fileResult.setStatusCode(HttpStatus.NOT_FOUND.value());

        PciAfpTask taskInfo = pciAfpRestService.queryTaskRecordByJobId(jobId);

        if (null == taskInfo) {
            msg = "不存在该" + jobId + "任务信息";
            log.info(msg);
            fileResult.setMsg(msg);
            return fileResult;
        }

        // 获取pci规划任务中的待优化小区字段
        String optimizeCellStr = taskInfo.getOptimizeCells();
        if (null == optimizeCellStr || optimizeCellStr.isEmpty()) {
            msg = "不存在变PCI小区表！";
            log.info(msg);
            fileResult.setMsg(msg);
            return fileResult;
        }

        List<String> optimizeCells = new ArrayList<>();
        for (String c : optimizeCellStr.trim().split(",")) {
            if (!"".equals(c.trim())) {
                optimizeCells.add(c);
            }
        }

        // 区域ID
        long cityId = taskInfo.getAreaId();
        Map<String, Cell> cellMap = pciAfpDataService.getLteCellMapByCityId(cityId);

        if (cellMap.isEmpty()) {
            msg = "该区域" + cityId + "不存在系统小区信息";
            log.info(msg);
            fileResult.setMsg(msg);
            return fileResult;
        }

        // 确定临时目录存在
        Path tmpDir = null;
        try {
            // 确定临时目录存在
            Path downloadPath = Paths.get(System.getProperty("java.io.tmpdir"), "download");
            Files.createDirectories(downloadPath);
            tmpDir = Files.createTempDirectory(downloadPath, "pci");
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (null == tmpDir || Files.notExists(tmpDir)) {
            msg = "创建临时目录失败";
            log.info(msg);
            fileResult.setMsg(msg);
            return fileResult;
        }

        List<String> sourceFileList = new ArrayList<>();
        // 下载的Pci规划文件名称
        String dlFileName = jobId + "_PCI优化方案.xlsx";
        // 下载的Pci规划文件全路径
        String dlFileRealPath = Paths.get(tmpDir.toString(), dlFileName).toString();

        try {
            List<PlanItem> bestPlanRes = pciAfpRestService.queryBestPlanByJobId(jobId);
            List<Map<String, Object>> res = collectResult(bestPlanRes, cellMap, optimizeCells);

            if (FileTool.createExcelFile(dlFileRealPath, res)) {
                sourceFileList.add(dlFileRealPath);
            }

            String outFileName = dlFileName;
            Path outFilePath = Paths.get(dlFileRealPath);
            if (sourceFileList.size() > 1) {
                outFileName = jobId + "_PCI优化.zip";
                outFilePath = Paths.get(ZipFileHandler.add2Zip(sourceFileList, outFileName));
            }

            if (Files.exists(outFilePath)) {
                msg = "文件下载成功。";
                log.debug(msg);
                fileResult.setResult(true);
                fileResult.setMsg(msg);
                fileResult.setStatusCode(HttpStatus.CREATED.value());
                fileResult.setFilename(outFileName);

                byte[] fileBody = Files.readAllBytes(outFilePath);
                fileResult.setFileBody(fileBody);
                fileResult.setFileLength(fileBody.length);
            } else {
                msg = "Pci规划结果文件不存在！,文件路径:" + outFilePath;
                log.error(msg);
                fileResult.setMsg(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
            msg = "获取Pci规划结果源文件中，读取数据出错";
            log.info(msg);
            fileResult.setMsg(msg);
        }
        return fileResult;
    }

    private List<Map<String, Object>> collectResult(List<PlanItem> list, Map<String, Cell> cellMap, List<String> optimizeCells) {
        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, Object> one;
        String cellId;
        Cell cell;
        for (PlanItem item : list) {
            one = new HashMap<>();
            cellId = item.getCellId();
            cell = cellMap.get(cellId);
            if (cell == null) {
                log.info("小区找不到对应工参数据：cellId ={}", cellId);
                one.put("cellName", "未知小区");
                one.put("oldPci", -1);
                one.put("oldEarfcn", -1);
            } else {
                // 小区名，pci,频点
                one.put("cellName", cell.getName());
                one.put("oldPci", cell.getPci());
                one.put("oldEarfcn", cell.getEarfcn());
            }
            one.put("cellId", cellId);
            int newEarfcn = item.getEarfcn();
            if (newEarfcn == -1) {
                if (cell == null) {
                    one.put("newEarfcn", "找不到对应工参数据");
                } else {
                    one.put("newEarfcn", "找不到对应MR数据 ");
                }
            } else {
                one.put("newEarfcn", newEarfcn);
            }
            int newPci = item.getPci();
            if (newPci == -1) {
                if (cell == null) {
                    one.put("newPci", "找不到对应工参数据");
                } else {
                    one.put("newPci", "找不到对应MR数据");
                }
            } else {
                one.put("newPci", newPci);
            }
            one.put("oriInterVal", item.getOldInterVal());
            one.put("interVal", item.getNewInterVal());

            if (optimizeCells.contains(cellId)) {
                one.put("remark", "修改小区");
            } else {
                one.put("remark", "MR其他小区");
            }
            result.add(one);
        }
        return result;
    }
}
