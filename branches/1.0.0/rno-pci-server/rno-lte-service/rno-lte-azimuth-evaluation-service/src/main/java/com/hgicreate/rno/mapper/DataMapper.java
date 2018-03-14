package com.hgicreate.rno.mapper;

import com.hgicreate.rno.model.AzimuthEvalResult;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * MyBatis接口
 *
 * @author li.tf
 * @date 2018-01-12 13:53:22
 */
@Mapper
public interface DataMapper {

    /**
     * 获取最新的测量时间的年月
     *
     * @return 年（key="year"）月（key="month"）
     * @date 2018-01-12 13:58:34
     */
    Map<String, Object> getNewestYearAndMonth();

    /**
     * 计算方位角
     *
     * @param map 计算条件
     * @return 方位角评估结果集
     * @date 2018-01-12 13:58:44
     */
    List<AzimuthEvalResult> calcAzimuth(Map<String, Object> map);

    /**
     * 通过任务ID获取方位角评估结果
     *
     * @param jobId 任务ID
     * @return 方位角评估结果集
     * @date 2018-01-12 13:59:33
     */
    List<Map<String, Object>> findAzimuthEvalResultsByJobId(long jobId);

    /**
     * 通过任务ID获取1000条方位角评估结果
     *
     * @param jobId 任务ID
     * @return 方位角评估结果
     * @date 2018-01-12 13:59:59
     */
    List<Map<String, Object>> findTop1000AzimuthEvalResultsByJobId(long jobId);

}
