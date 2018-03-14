package com.hgicreate.rno.lteazimuthevaluation.service;

import com.hgicreate.rno.lteazimuthevaluation.dao.RnoLteAzimuthEvaluationDao;
import com.hgicreate.rno.lteazimuthevaluation.dao.RnoLteAzimuthEvaluationSparkDao;
import com.hgicreate.rno.lteazimuthevaluation.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class RnoLteAzimuthEvaluationServiceImpl implements RnoLteAzimuthEvaluationService {
    private static final Logger logger = LoggerFactory.getLogger(RnoLteAzimuthEvaluationServiceImpl.class);

    private RnoLteAzimuthEvaluationDao rnoLteAzimuthEvaluationDao;

    private RnoLteAzimuthEvaluationSparkDao rnoLteAzimuthEvaluationSparkDao;

    public RnoLteAzimuthEvaluationServiceImpl(RnoLteAzimuthEvaluationDao rnoLteAzimuthEvaluationDao, RnoLteAzimuthEvaluationSparkDao rnoLteAzimuthEvaluationSparkDao) {
        this.rnoLteAzimuthEvaluationDao = rnoLteAzimuthEvaluationDao;
        this.rnoLteAzimuthEvaluationSparkDao = rnoLteAzimuthEvaluationSparkDao;
    }

    @Override
    public List<TaskQueryResult> queryLteAzimuthEvaluationTaskByPage(TaskQueryCond cond, Page page) {
        logger.info("进入方法：queryLteAzimuthEvaluationTaskByPage。condition={},page={}", cond, page);
        if (page == null) {
            logger.warn("方法：queryLteAzimuthEvaluationTaskByPage的参数：page 是空！无法查询!");
            return Collections.emptyList();
        }

        long totalCnt = page.getTotalCnt();
        if (totalCnt < 0) {
            totalCnt = rnoLteAzimuthEvaluationDao.getLteAzimuthEvaluationJobCount(cond);
            page.setTotalCnt((int) totalCnt);
        }

        int startIndex = page.calculateStart();
        cond.setStartIndex(startIndex);
        cond.setEndIndex(startIndex + page.getPageSize());

        return rnoLteAzimuthEvaluationDao.queryLteAzimuthEvaluationTaskByPage(cond);
    }

    @Override
    public JobProfile getJobProfileByJobId(long jobId) {
        return rnoLteAzimuthEvaluationDao.getJobProfileByJobId(jobId);
    }

    @Override
    public Long getOneJob(JobProfile job) {
        return rnoLteAzimuthEvaluationDao.getOneJob(job);
    }

    @Override
    public Long updateLteAzimuthEvaluationStatus(long jobId, String jobStatus) {
       return rnoLteAzimuthEvaluationDao.updateLteAzimuthEvaluationStatus(jobId, jobStatus);
    }

    @Override
    public SubmitResult submitLteAzimuthEvaluationTask(String account, AzimuthJobRecord taskInfo) {
        logger.debug("进入submitLteAzimuthEvaluationTask account={} ,taskInfo={}", account, taskInfo);
        SubmitResult result = new SubmitResult();
        // 创建job
        JobProfile job = new JobProfile();
        job.setAccount(account);
        job.setJobName(taskInfo.getTaskName());
        job.setJobType("RNO_LTE_AZIMUTH_EVALUATION");

        job.setSubmitTime(new Date());
        job.setDescription(taskInfo.getTaskDesc());
        job.setJobStateStr(JobStatus.Waiting.toString());
        /*对rno_ms_job表插入一条job*/
        rnoLteAzimuthEvaluationDao.addOneJob(job);

        long jobId = job.getJobId();
        String msg;
        if (jobId == 0) {
            msg = "创建jobId失败！";
            logger.error(msg);
            result.setDesc(msg);
        }

        taskInfo.setJobId(jobId);

        taskInfo.setDlFileName(taskInfo.getCityName() + "_" + jobId + "_LTE方位角评估结果.csv");

        // 创建日期
        String createTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        taskInfo.setCreateTime(createTime);
        taskInfo.setModTime(createTime);

        taskInfo.setFinishState("排队中");

        // 执行
        try {
            rnoLteAzimuthEvaluationDao.addLteAzimuthEvaluationJob(taskInfo);
            result.setFlag(true);
            result.setJobId(jobId);
            result.setDesc("提交任务成功！");
        } catch (Exception e) {
            e.printStackTrace();
            msg = "jobId=" + jobId + "，保存Lte方位角评估计算任务失败！";
            logger.error(msg);
            result.setJobId(jobId);
            result.setDesc(msg);
        }
        logger.debug("退出submitLteAzimuthEvaluationTask result= {}", result);
        return result;
    }

    @Override
    public List<AzimuthJobRecord> queryLteAzimuthEvaluationJobRecByJobId(long jobId) {
        return rnoLteAzimuthEvaluationDao.queryLteAzimuthEvaluationJobRecByJobId(jobId);
    }

    @Override
    public List<AzimuthResult> calcAzimuth(long cityId, String begTime, String endTime) {
        return rnoLteAzimuthEvaluationSparkDao.calcAzimuth(cityId, begTime, endTime);
    }

    @Override
    public Long batchInsertAzimuthResult(long jobId, List<AzimuthResult> results, int batch) {
        return rnoLteAzimuthEvaluationDao.batchInsertAzimuthResult(jobId, results, batch);
    }

    @Override
    public List<AzimuthResult> queryAzimuthResultsByJobId(long jobId) {
        return rnoLteAzimuthEvaluationDao.queryAzimuthResultsByJobId(jobId);
    }

    @Override
    public boolean hasData(String evalType, long cityId, String begTime, String endTime) {
        long mrCnt = rnoLteAzimuthEvaluationSparkDao.queryLteMrDataCnt(cityId, begTime, endTime);
        long structureCnt = rnoLteAzimuthEvaluationSparkDao.queryLteStructureDataCnt(cityId, begTime, endTime);
        boolean flag;
        if ("type1".equalsIgnoreCase(evalType)) {
            flag = mrCnt > 0;
        } else if ("type2".equalsIgnoreCase(evalType)) {
            flag = structureCnt > 0;
        } else {
            flag = mrCnt > 0 || structureCnt > 0;
        }
        return flag;
    }
}
