package com.hgicreate.rno.lteazimuthevaluation.dao;

import com.hgicreate.rno.lteazimuthevaluation.mapper.spark.SparkMapper;
import com.hgicreate.rno.lteazimuthevaluation.model.AzimuthResult;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chen.c10 on 2016/12/16.
 * spark dao impl
 */
@Repository
public class RnoLteAzimuthEvaluationSparkDaoImpl implements RnoLteAzimuthEvaluationSparkDao {

    private SparkMapper sparkMapper;

    public RnoLteAzimuthEvaluationSparkDaoImpl(SparkMapper sparkMapper) {
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
    public List<AzimuthResult> calcAzimuth(long cityId, String begTime, String endTime) {
        Map<String, Object> map = new HashMap<>();
        map.put("cityId", cityId);
        map.put("begTime", begTime);
        map.put("endTime", endTime);
        return sparkMapper.calcAzimuth(map);
    }
}
