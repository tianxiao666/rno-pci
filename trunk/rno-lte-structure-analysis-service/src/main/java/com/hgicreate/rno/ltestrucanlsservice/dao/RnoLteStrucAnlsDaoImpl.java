package com.hgicreate.rno.ltestrucanlsservice.dao;

import com.hgicreate.rno.ltestrucanlsservice.mapper.oracle.OracleMapper;
import com.hgicreate.rno.ltestrucanlsservice.model.*;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

@Repository
public class RnoLteStrucAnlsDaoImpl implements RnoLteStrucAnlsDao {

    private OracleMapper oracleMapper;

    public RnoLteStrucAnlsDaoImpl(OracleMapper oracleMapper) {
        this.oracleMapper = oracleMapper;
    }

    @Override
    public Long addLteStrucAnlsTask(StrucTaskRecord cond) {
        return oracleMapper.addLteStrucAnlsTask(cond);
    }

    @Override
    public StrucTaskRecord queryLteStrucAnlsTaskRecByJobId(long jobId) {
        return oracleMapper.queryLteStrucAnlsTaskRecByJobId(jobId);
    }

    @Override
    public Long queryLteStrucAnlsTaskCnt(TaskQueryCond cond) {
        return oracleMapper.queryLteStrucAnlsTaskCnt(cond);
    }

    @Override
    public List<TaskQueryResult> queryLteStrucAnlsTaskByPage(TaskQueryCond cond) {
        return oracleMapper.queryLteStrucAnlsTaskByPage(cond);
    }

    @Override
    public Long updateLteStrucAnlsTaskStatus(long jobId, String jobStatus) {
        Map<String, Object> map = new HashMap<>();
        map.put("jobId", jobId);
        map.put("jobStatus", jobStatus);
        return oracleMapper.updateLteStrucAnlsTaskStatus(map);
    }

    @Override
    public Long stopLteStrucAnlsTaskById(Long jobId) {
        return oracleMapper.stopLteStrucAnlsTaskById(jobId);
    }

    @Override
    public Long saveOverlapCover(long jobId, List<OverlapCover> overlapCovers, int batch) {
        final Map<String, Object> map = new HashMap<>();
        final AtomicLong cnt = new AtomicLong();
        map.put("jobId", jobId);
        int size = overlapCovers.size();
        IntStream.range(0, (int) Math.ceil((double) size / batch)).forEach(e -> {
            int start = e * batch;
            int end = (e + 1) * batch;
            map.put("list", new ArrayList<>(overlapCovers.subList(start, end > size ? size : end)));
            cnt.addAndGet(oracleMapper.batchInsertOverlapCoverResult(map));
        });

        return cnt.get();
    }

    @Override
    public List<OverlapCover> queryOverlapCoverResultByJobId(long jobId) {
        return oracleMapper.queryOverlapCoverResultByJobId(jobId);
    }

    @Override
    public Long saveOverCover(long jobId, List<OverCover> overCovers, int batch) {
        final Map<String, Object> map = new HashMap<>();
        final AtomicLong cnt = new AtomicLong();
        map.put("jobId", jobId);
        int size = overCovers.size();
        IntStream.range(0, (int) Math.ceil((double) size / batch)).forEach(e -> {
            int start = e * batch;
            int end = (e + 1) * batch;
            map.put("list", new ArrayList<>(overCovers.subList(start, end > size ? size : end)));
            cnt.addAndGet(oracleMapper.batchInsertOverCoverResult(map));
        });

        return cnt.get();
    }

    @Override
    public List<OverCover> queryOverCoverResultByJobId(long jobId) {
        return oracleMapper.queryOverCoverResultByJobId(jobId);
    }

    @Override
    public Long saveMetricsSummary(long jobId, List<MetricsSummary> metricsSummaries, int batch) {
        final Map<String, Object> map = new HashMap<>();
        final AtomicLong cnt = new AtomicLong();
        map.put("jobId", jobId);
        int size = metricsSummaries.size();
        IntStream.range(0, (int) Math.ceil((double) size / batch)).forEach(e -> {
            int start = e * batch;
            int end = (e + 1) * batch;
            map.put("list", new ArrayList<>(metricsSummaries.subList(start, end > size ? size : end)));
            cnt.addAndGet(oracleMapper.batchInsertMetricsSummaryResult(map));
        });

        return cnt.get();
    }

    @Override
    public List<MetricsSummary> queryMetricsSummaryResultByJobId(long jobId) {
        return oracleMapper.queryMetricsSummaryResultByJobId(jobId);
    }
}
