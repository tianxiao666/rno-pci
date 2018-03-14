package com.hgicreate.rno.ltestrucanlsservice.dao;

import com.hgicreate.rno.ltestrucanlsservice.mapper.spark.SparkMapper;
import com.hgicreate.rno.ltestrucanlsservice.model.MetricsSummary;
import com.hgicreate.rno.ltestrucanlsservice.model.OverCover;
import com.hgicreate.rno.ltestrucanlsservice.model.OverlapCover;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class RnoLteStrucAnlsSparkDaoImpl implements RnoLteStrucAnlsSparkDao {

    private SparkMapper sparkMapper;

    public RnoLteStrucAnlsSparkDaoImpl(SparkMapper sparkMapper) {
        this.sparkMapper = sparkMapper;
    }

    @Override
    public Long queryLteMrDataCnt(long cityId, String begTime, String endTime) {
        Map<String, Object> map = new HashMap<>();
        map.put("cityId", cityId);
        map.put("begTime", begTime);
        map.put("endTime", endTime);
        return sparkMapper.queryLteMrDataCnt(map);
    }

    @Override
    public Long queryLteStructureDataCnt(long cityId, String begTime, String endTime) {
        Map<String, Object> map = new HashMap<>();
        map.put("cityId", cityId);
        map.put("begTime", begTime);
        map.put("endTime", endTime);
        return sparkMapper.queryLteStructureDataCnt(map);
    }

    @Override
    public List<OverlapCover> calcOverlapCover(long cityId, String begTime, String endTime) {
        Map<String, Object> map = new HashMap<>();
        map.put("cityId", cityId);
        map.put("begTime", begTime);
        map.put("endTime", endTime);
        return sparkMapper.calcOverlapCover(map);
    }

    @Override
    public List<OverCover> calcOverCover(long cityId, String begTime, String endTime) {
        Map<String, Object> map = new HashMap<>();
        map.put("cityId", cityId);
        map.put("begTime", begTime);
        map.put("endTime", endTime);
        return sparkMapper.calcOverCover(map);
    }

    @Override
    public Long handleOverCover(long jobId, long cityId, String begTime, String endTime) {
        Map<String, Object> map = new HashMap<>();
        map.put("jobId", jobId);
        map.put("cityId", cityId);
        map.put("begTime", begTime);
        map.put("endTime", endTime);
        return sparkMapper.handleOverCover(map);
    }

    @Override
    public List<OverCover> queryOverCoverResultByJobId(long jobId) {
        return sparkMapper.queryOverCoverResultByJobId(jobId);
    }

    @Override
    public List<MetricsSummary> calcMetricsSummary(long cityId, String begTime, String endTime) {
        Map<String, Object> map = new HashMap<>();
        map.put("cityId", cityId);
        map.put("begTime", begTime);
        map.put("endTime", endTime);
        return sparkMapper.calcMetricsSummary(map);
    }
}
