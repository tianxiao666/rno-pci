package com.hgicreate.rno.ltestrucanlsservice.mapper.oracle;

import com.hgicreate.rno.ltestrucanlsservice.model.*;

import java.util.List;
import java.util.Map;

public interface CommonMapper {
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
    Long queryReportCnt(long jobId);

    /**
     * 分页查询某job的报告
     */
    List<Report> queryReportByPage(Map<String, Object> map);

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
     * 根据任务ID获取任务名称
     */
    JobProfile getJobByJobId(long jobId);

    /**
     * 停止一个任务
     */
    Long stopJobByJobId(long jobId);

    /**
     * 检查任务状态
     */
    JobStatus checkJobStatus(long jobId);
}
