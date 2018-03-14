package com.hgicreate.rno.lteazimuthevaluation.dao;

import com.hgicreate.rno.lteazimuthevaluation.mapper.oracle.OracleMapper;
import com.hgicreate.rno.lteazimuthevaluation.model.Area;
import com.hgicreate.rno.lteazimuthevaluation.model.JobProfile;
import com.hgicreate.rno.lteazimuthevaluation.model.JobStatus;
import com.hgicreate.rno.lteazimuthevaluation.model.Report;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class RnoCommonDaoImpl implements RnoCommonDao {

    private OracleMapper oracleMapper;

    public RnoCommonDaoImpl(OracleMapper oracleMapper) {
        this.oracleMapper = oracleMapper;
    }

    @Override
    public Long queryAccountCnt(String account) {
        return oracleMapper.queryAccountCnt(account);
    }

    @Override
    public List<Area> getSpecialLevelAreaByAccount(String account, long parentId, String areaLevel) {
        Map<String, Object> map = new HashMap<>();
        map.put("account", account);
        map.put("parentId", parentId);
        map.put("areaLevel", areaLevel);
        return oracleMapper.getSpecialLevelAreaByAccount(map);
    }

    @Override
    public Area getUserAreaByAccount(String account) {
        return oracleMapper.getUserAreaByAccount(account);
    }

    @Override
    public Long addOneReport(Report report) {
        return oracleMapper.addOneReport(report);
    }

    @Override
    public Long queryJobReportCnt(long jobId) {
        return oracleMapper.queryJobReportCnt(jobId);
    }

    @Override
    public List<Report> queryJobReportByPage(Map<String, Object> map) {
        return oracleMapper.queryJobReportByPage(map);
    }

    @Override
    public Long updateJobBegTime(JobProfile job) {
        return oracleMapper.updateJobBegTime(job);
    }

    @Override
    public Long updateJobEndTime(JobProfile job) {
        return oracleMapper.updateJobEndTime(job);
    }

    @Override
    public Long updateJobRunningStatus(JobProfile job) {
        return oracleMapper.updateJobRunningStatus(job);
    }

    @Override
    public Long stopJobByJobId(long jobId) {
        return oracleMapper.stopJobByJobId(jobId);
    }

    @Override
    public JobStatus checkJobStatus(long jobId) {
        return oracleMapper.checkJobStatus(jobId);
    }
}
