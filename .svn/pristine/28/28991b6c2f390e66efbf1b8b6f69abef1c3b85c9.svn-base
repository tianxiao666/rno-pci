package com.hgicreate.rno.ltestrucanlsservice.mapper.spark;

import com.hgicreate.rno.ltestrucanlsservice.model.MetricsSummary;
import com.hgicreate.rno.ltestrucanlsservice.model.OverCover;
import com.hgicreate.rno.ltestrucanlsservice.model.OverlapCover;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface SparkMapper {
    Long queryLteMrDataCnt(Map<String, Object> map);

    Long queryLteStructureDataCnt(Map<String, Object> map);

    List<OverlapCover> calcOverlapCover(Map<String, Object> map);

    List<OverCover> calcOverCover(Map<String, Object> map);

    Long handleOverCover(Map<String, Object> map);

    List<OverCover> queryOverCoverResultByJobId(long jobId);

    List<MetricsSummary> calcMetricsSummary(Map<String, Object> map);
}
