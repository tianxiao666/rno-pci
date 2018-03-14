package com.hgicreate.rno.ltestrucanlsservice.service;

import com.hgicreate.rno.ltestrucanlsservice.dao.RnoLteStrucAnlsDao;
import com.hgicreate.rno.ltestrucanlsservice.dao.RnoLteStrucAnlsSparkDao;
import com.hgicreate.rno.ltestrucanlsservice.model.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RnoLteStrucAnlsServiceImpl implements RnoLteStrucAnlsService {
    private RnoLteStrucAnlsDao rnoLteStrucAnlsDao;
    private RnoLteStrucAnlsSparkDao rnoLteStrucAnlsSparkDao;

    public RnoLteStrucAnlsServiceImpl(RnoLteStrucAnlsDao rnoLteStrucAnlsDao, RnoLteStrucAnlsSparkDao rnoLteStrucAnlsSparkDao) {
        this.rnoLteStrucAnlsDao = rnoLteStrucAnlsDao;
        this.rnoLteStrucAnlsSparkDao = rnoLteStrucAnlsSparkDao;
    }

    @Override
    public Long queryLteStrucAnlsTaskCnt(TaskQueryCond cond) {
        return rnoLteStrucAnlsDao.queryLteStrucAnlsTaskCnt(cond);
    }

    @Override
    public List<TaskQueryResult> queryLteStrucAnlsTaskByPage(TaskQueryCond cond) {
        return rnoLteStrucAnlsDao.queryLteStrucAnlsTaskByPage(cond);
    }

    @Override
    public Boolean addLteStrucAnlsTask(StrucTaskRecord task) {
        Long result = rnoLteStrucAnlsDao.addLteStrucAnlsTask(task);
        return result != null && result > 0;
    }

    @Override
    public StrucTaskRecord queryLteStrucAnlsTaskRecByJobId(long jobId) {
        return rnoLteStrucAnlsDao.queryLteStrucAnlsTaskRecByJobId(jobId);
    }

    @Override
    public Long updateLteStrucAnlsTaskStatus(long jobId, String jobStatus) {
        return rnoLteStrucAnlsDao.updateLteStrucAnlsTaskStatus(jobId, jobStatus);
    }

    @Override
    public Long stopLteStrucAnlsTaskById(Long jobId) {
        return rnoLteStrucAnlsDao.stopLteStrucAnlsTaskById(jobId);
    }

    @Override
    public boolean hasData(long cityId, String begTime, String endTime) {
        long mrCnt = rnoLteStrucAnlsSparkDao.queryLteMrDataCnt(cityId, begTime, endTime);
        long structureCnt = rnoLteStrucAnlsSparkDao.queryLteStructureDataCnt(cityId, begTime, endTime);
        return mrCnt > 0 && structureCnt > 0;
    }

    @Override
    public List<OverlapCover> calcOverlapCover(long cityId, String begTime, String endTime) {
        return rnoLteStrucAnlsSparkDao.calcOverlapCover(cityId, begTime, endTime);
    }

    @Override
    public Long saveOverlapCover(long jobId, List<OverlapCover> overlapCovers, int batch) {
        return rnoLteStrucAnlsDao.saveOverlapCover(jobId, overlapCovers, batch);
    }

    @Override
    public List<OverlapCover> queryOverlapCoverResultByJobId(long jobId) {
        return rnoLteStrucAnlsDao.queryOverlapCoverResultByJobId(jobId);
    }

    @Override
    public List<OverCover> calcOverCover(long cityId, String begTime, String endTime) {
        return rnoLteStrucAnlsSparkDao.calcOverCover(cityId, begTime, endTime);
    }

    @Override
    public Long saveOverCover(long jobId, List<OverCover> overCovers, int batch) {
        return rnoLteStrucAnlsDao.saveOverCover(jobId, overCovers, batch);
    }

    @Override
    public List<OverCover> queryOverCoverResultByJobId(long jobId) {
//        return rnoLteStrucAnlsDao.queryOverCoverResultByJobId(jobId);
        return rnoLteStrucAnlsSparkDao.queryOverCoverResultByJobId(jobId);
    }

    @Override
    public Long handleOverCover(long jobId, long cityId, String begTime, String endTime) {
        return rnoLteStrucAnlsSparkDao.handleOverCover(jobId, cityId, begTime, endTime);
    }

    @Override
    public List<MetricsSummary> calcMetricsSummary(long cityId, String begTime, String endTime) {
        return rnoLteStrucAnlsSparkDao.calcMetricsSummary(cityId, begTime, endTime);
    }

    @Override
    public Long saveMetricsSummary(long jobId, List<MetricsSummary> metricsSummaries, int batch) {
        return rnoLteStrucAnlsDao.saveMetricsSummary(jobId, metricsSummaries, batch);
    }

    @Override
    public List<MetricsSummary> queryMetricsSummaryResultByJobId(long jobId) {
        return rnoLteStrucAnlsDao.queryMetricsSummaryResultByJobId(jobId);
    }
}
