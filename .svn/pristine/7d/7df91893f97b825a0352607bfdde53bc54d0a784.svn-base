package com.hgicreate.rno.ltestrucanlsservice.dao;

import com.hgicreate.rno.ltestrucanlsservice.mapper.oracle.CommonMapper;
import com.hgicreate.rno.ltestrucanlsservice.model.Area;
import com.hgicreate.rno.ltestrucanlsservice.model.JobProfile;
import com.hgicreate.rno.ltestrucanlsservice.model.JobStatus;
import com.hgicreate.rno.ltestrucanlsservice.model.Report;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class RnoCommonDaoImpl implements RnoCommonDao {

    private CommonMapper commonMapper;

    public RnoCommonDaoImpl(CommonMapper commonMapper) {
        this.commonMapper = commonMapper;
    }

    @Override
    public Long queryAccountCnt(String account) {
        return commonMapper.queryAccountCnt(account);
    }

    @Override
    public List<Area> getSpecialLevelAreaByAccount(String account, long parentId, String areaLevel) {
        Map<String, Object> map = new HashMap<>();
        map.put("account", account);
        map.put("parentId", parentId);
        map.put("areaLevel", areaLevel);
        return commonMapper.getSpecialLevelAreaByAccount(map);
    }

    @Override
    public Area getUserAreaByAccount(String account) {
        return commonMapper.getUserAreaByAccount(account);
    }

    @Override
    public Long addOneReport(Report report) {
        return commonMapper.addOneReport(report);
    }

    @Override
    public Long queryReportCnt(long jobId) {
        return commonMapper.queryReportCnt(jobId);
    }

    @Override
    public List<Report> queryReportByPage(Map<String, Object> map) {
        return commonMapper.queryReportByPage(map);
    }

    @Override
    public Long addOneJob(JobProfile job) {
        return commonMapper.addOneJob(job);
    }

    @Override
    public Long updateJobBegTime(JobProfile job) {
        return commonMapper.updateJobBegTime(job);
    }

    @Override
    public Long updateJobEndTime(JobProfile job) {
        return commonMapper.updateJobEndTime(job);
    }

    @Override
    public Long updateJobRunningStatus(JobProfile job) {
        return commonMapper.updateJobRunningStatus(job);
    }

    @Override
    public Long getOneJob(JobProfile job) {
        return commonMapper.getOneJob(job);
    }

    @Override
    public JobProfile getJobByJobId(long jobId) {
        return commonMapper.getJobByJobId(jobId);
    }

    @Override
    public Long stopJobByJobId(long jobId) {
        return commonMapper.stopJobByJobId(jobId);
    }

    @Override
    public JobStatus checkJobStatus(long jobId) {
        return commonMapper.checkJobStatus(jobId);
    }

}
