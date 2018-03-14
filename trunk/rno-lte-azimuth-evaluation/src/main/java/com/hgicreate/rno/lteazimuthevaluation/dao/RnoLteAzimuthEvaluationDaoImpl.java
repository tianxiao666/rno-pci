package com.hgicreate.rno.lteazimuthevaluation.dao;

import com.hgicreate.rno.lteazimuthevaluation.mapper.oracle.OracleMapper;
import com.hgicreate.rno.lteazimuthevaluation.model.*;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

@Repository
public class RnoLteAzimuthEvaluationDaoImpl implements RnoLteAzimuthEvaluationDao {

    private OracleMapper oracleMapper;

    public RnoLteAzimuthEvaluationDaoImpl(OracleMapper oracleMapper) {
        this.oracleMapper = oracleMapper;
    }

    @Override
    public Long addOneJob(JobProfile job) {
        return oracleMapper.addOneJob(job);
    }

    @Override
    public Long addLteAzimuthEvaluationJob(AzimuthJobRecord cond) {
        return oracleMapper.addLteAzimuthEvaluationJob(cond);
    }

    @Override
    public Long getLteAzimuthEvaluationJobCount(TaskQueryCond cond) {
        return oracleMapper.getLteAzimuthEvaluationJobCount(cond);
    }

    @Override
    public List<AzimuthJobRecord> queryLteAzimuthEvaluationJobRecByJobId(long jobId) {
        return oracleMapper.queryLteAzimuthEvaluationJobRecByJobId(jobId);
    }

    @Override
    public Long getOneJob(JobProfile job) {
        return oracleMapper.getOneJob(job);
    }

    @Override
    public JobProfile getJobProfileByJobId(long jobId) {
        return oracleMapper.getJobProfileByJobId(jobId);
    }

    @Override
    public Long updateLteAzimuthEvaluationStatus(long jobId, String jobStatus) {
        Map<String, Object> map = new HashMap<>();
        map.put("jobId", jobId);
        map.put("jobStatus", jobStatus);
        return oracleMapper.updateLteAzimuthEvaluationStatus(map);
    }

    @Override
    public List<TaskQueryResult> queryLteAzimuthEvaluationTaskByPage(TaskQueryCond cond) {
        return oracleMapper.queryLteAzimuthEvaluationTaskByPage(cond);
    }

    @Override
    public Long batchInsertAzimuthResult(long jobId, List<AzimuthResult> results, int batch) {
        final Map<String, Object> map = new HashMap<>();
        final AtomicLong cnt = new AtomicLong();
        map.put("jobId", jobId);

        final Map<Integer, List<AzimuthResult>> group = new HashMap<>();
        IntStream.range(0, results.size()).forEach(e -> {
            int gid = e / batch;
            group.computeIfAbsent(gid, k -> new ArrayList<>()).add(results.get(e));
        });

        // 由于是绑定参数的提交方式，第一次耗时比较多，之后可以重用，速度较快。
        group.values().forEach(e -> {
            map.put("list", e);
            cnt.addAndGet(oracleMapper.batchInsertAzimuthResult(map));
        });

        return cnt.get();
    }

    @Override
    public List<AzimuthResult> queryAzimuthResultsByJobId(long jobId) {
        return oracleMapper.queryAzimuthResultsByJobId(jobId);
    }
}
