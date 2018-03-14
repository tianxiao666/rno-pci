package com.hgicreate.rno.lteazimuthevaluation.mapper.spark;

import com.hgicreate.rno.lteazimuthevaluation.model.AzimuthResult;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * Created by chen.c10 on 2016/12/16.
 * Spark mapper 类
 */
@Mapper
public interface SparkMapper {
    Long queryLteMrDataCnt(Map<String, Object> map);

    Long queryLteStructureDataCnt(Map<String, Object> map);

    List<AzimuthResult> calcAzimuth(Map<String, Object> map);
}
