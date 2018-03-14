package com.hgicreate.rno.lteazimuthevaluation.dao;

import com.hgicreate.rno.lteazimuthevaluation.model.AzimuthResult;

import java.util.List;
import java.util.Map;

/**
 * Created by chen.c10 on 2016/12/16.
 * spark Dao interface
 */
public interface RnoLteAzimuthEvaluationSparkDao {
    Long queryLteMrDataCnt(long cityId, String begTime, String endTime);

    Long queryLteStructureDataCnt(long cityId, String begTime, String endTime);

    List<AzimuthResult> calcAzimuth(long cityId, String begTime, String endTime);
}
