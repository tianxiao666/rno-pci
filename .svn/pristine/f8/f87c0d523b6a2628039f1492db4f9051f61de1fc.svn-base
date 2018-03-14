package com.hgicreate.rno.lteazimuthevaluation.mapper.oracle;

import com.hgicreate.rno.lteazimuthevaluation.model.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface OracleMapper {

    /**
     * 查询账户数量
     */
    Long queryAccountCnt(String account);

    /**
     * 获取用户可访问区域
     */
    List<Area> getSpecialLevelAreaByAccount(Map<String, Object> map);

    /**
     * 增加一条新的报告记录
     */
    Long addOneReport(Report report);

    /**
     * 获取用户的默认城市区域
     */
    Area getUserAreaByAccount(String account);

    /**
     * 查询某job的报告数量
     */
    Long queryJobReportCnt(long jobId);

    /**
     * 分页查询某job的报告
     */
    List<Report> queryJobReportByPage(Map<String, Object> map);

    /**
     * 添加一条任务信息
     */
    Long addOneJob(JobProfile job);

    /**
     * 更新任务开始时间
     */
    Long updateJobBegTime(JobProfile job);

    /**
     * 更新任务结束时间
     */
    Long updateJobEndTime(JobProfile job);

    /**
     * 更新任务运行状态
     */
    Long updateJobRunningStatus(JobProfile job);

    /**
     * 获取相应状态下的任务ID
     */
    Long getOneJob(JobProfile job);

    /**
     * 通过 jobId 获取Lte方位角计算记录信息
     */
    List<AzimuthJobRecord> queryLteAzimuthEvaluationJobRecByJobId(long jobId);

    /**
     * 更新PCI规划的job状态
     */
    Long updateLteAzimuthEvaluationStatus(Map<String, Object> map);

    /**
     * 根据任务ID获取任务名称
     */
    JobProfile getJobProfileByJobId(long jobId);

    /**
     * 获取LTE方位角评估分析任务的总数
     */
    Long getLteAzimuthEvaluationJobCount(TaskQueryCond cond);

    /**
     * 分页获取LTE方位角评估分析任务的信息
     */
    List<TaskQueryResult> queryLteAzimuthEvaluationTaskByPage(TaskQueryCond cond);

    /**
     * 保存Lte方位角工作信息
     */
    Long addLteAzimuthEvaluationJob(AzimuthJobRecord cond);

    /**
     * 批量导入计算结果到数据库
     */
    Long batchInsertAzimuthResult(Map<String, Object> map);

    /**
     * 通过任务ID查找任务结果
     */
    List<AzimuthResult> queryAzimuthResultsByJobId(long jobId);

    /**
     * 停止一个任务
     */
    Long stopJobByJobId(long jobId);

    /**
     * 检查任务状态
     */
    JobStatus checkJobStatus(long jobId);
}
