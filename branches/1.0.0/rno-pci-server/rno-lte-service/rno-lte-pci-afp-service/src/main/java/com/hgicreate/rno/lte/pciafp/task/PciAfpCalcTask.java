package com.hgicreate.rno.lte.pciafp.task;

import com.hgicreate.rno.lte.pciafp.model.DataParseStatus;
import com.hgicreate.rno.lte.pciafp.model.Job;
import com.hgicreate.rno.lte.pciafp.model.JobStatus;
import com.hgicreate.rno.lte.pciafp.model.TaskState;
import com.hgicreate.rno.lte.pciafp.service.CommonRestService;
import com.hgicreate.rno.lte.pciafp.service.PciAfpRestService;
import com.hgicreate.rno.lte.pciafp.service.PciAfpDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author chen.c10
 */
@Slf4j
@Component
public class PciAfpCalcTask implements JobExecutor {

    private final CommonRestService commonRestService;

    private final PciAfpRestService pciAfpRestService;

    private final PciAfpDataService pciAfpDataService;

    public PciAfpCalcTask(CommonRestService commonRestService, PciAfpRestService pciAfpRestService, PciAfpDataService pciAfpDataService) {
        this.commonRestService = commonRestService;
        this.pciAfpRestService = pciAfpRestService;
        this.pciAfpDataService = pciAfpDataService;
    }

    @Override
    public boolean runJobInternal(long jobId) {

        // 任务状态检查点
        if (commonRestService.isJobStopped(jobId)) {
            log.debug("LTE结构优化分析任务被停止");
            return false;
        }

        Job job = commonRestService.getJobByJobId(jobId);
        // 开始任务，更新任务状态
        commonRestService.startJob(jobId);

        String msg;
        Date startTime = new Date();
        pciAfpRestService.updateOwnProgress(jobId, TaskState.Starting.toString());

        PciAfpCalcConfig config = new PciAfpCalcConfig(jobId, commonRestService, pciAfpRestService, pciAfpDataService);

        pciAfpRestService.updateOwnProgress(jobId, "正在计算");
        if (!config.buildPciTaskConf()) {
            msg = "+++>>>名称=" + job.getJobName() + ",结果：任务失败！原因：初始化失败。";
            log.error(msg);
            // 保存报告信息
            commonRestService.addOneReport(jobId, startTime, msg, DataParseStatus.Fail.toString(), "任务初始化");
            pciAfpRestService.updateOwnProgress(jobId, "计算失败");
            commonRestService.endJob(job, JobStatus.Fail.toString());
            return false;
        }

        // 读取结果
        if (!config.readData()) {
            msg = "名称 = " + job.getJobName() + ", 结果：任务失败！原因：读取干扰数据失败！";
            log.error(msg);
            // 保存报告信息
            commonRestService.addOneReport(jobId, startTime, msg, DataParseStatus.Fail.toString(), "读取数据");
            pciAfpRestService.updateOwnProgress(jobId, "计算失败");
            commonRestService.endJob(job, JobStatus.Fail.toString());
            return false;
        }
        // 开始计算
        PciAfpCalcActuator pciCalc = new PciAfpCalcActuator(config, pciAfpRestService);
        try {
            pciCalc.execCalc();
        } catch (Exception e) {
            e.printStackTrace();
            msg = "名称 = " + job.getJobName() + ", 结果：任务失败！原因：计算失败！";
            log.error(msg);
            // 保存报告信息
            commonRestService.addOneReport(jobId, startTime, msg, DataParseStatus.Fail.toString(), "优化计算");
            pciAfpRestService.updateOwnProgress(jobId, "计算失败");
            commonRestService.endJob(job, JobStatus.Fail.toString());
            return false;
        }
        // 结束任务
        if (!"fail".equals(config.getReturnInfo())) {
            msg = "名称 = " + job.getJobName() + ", 结果：任务成功！<br>返回信息：<br>" + config.getReturnInfo();
        } else {
            msg = "名称 = " + job.getJobName() + ", 结果：任务成功！<br>读取返回信息失败！";
        }
        log.info(msg);
        // 保存报告信息
        commonRestService.addOneReport(jobId, startTime, msg, DataParseStatus.Suc.toString(), "优化计算");
        pciAfpRestService.updateOwnProgress(jobId, "计算完成");
        commonRestService.endJob(job, JobStatus.Succeeded.toString());
        return true;
    }
}
