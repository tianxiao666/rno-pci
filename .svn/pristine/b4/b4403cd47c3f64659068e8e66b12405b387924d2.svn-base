package com.hgicreate.rno.lteazimuthevaluation.dao;

import com.hgicreate.rno.lteazimuthevaluation.model.Area;
import com.hgicreate.rno.lteazimuthevaluation.model.JobProfile;
import com.hgicreate.rno.lteazimuthevaluation.model.JobStatus;
import com.hgicreate.rno.lteazimuthevaluation.model.Report;

import java.util.List;
import java.util.Map;

public interface RnoCommonDao {

    /**
     * 查询账户数量
     */
    Long queryAccountCnt(String account);

    /**
     * 获取用户可访问的指定级别的区域
     */
    List<Area> getSpecialLevelAreaByAccount(String account, long parentId, String areaLevel);

    /**
     * 获取用户的默认城市区域
     */
    Area getUserAreaByAccount(String account);

    /**
     * 增加一条新的报告记录
     */
    Long addOneReport(Report report);

    /**
     * 查询某job的报告数量
     */
    Long queryJobReportCnt(long jobId);

    /**
     * 分页查询某job的报告
     */
    List<Report> queryJobReportByPage(Map<String, Object> map);

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
     * 停止一个任务
     */
    Long stopJobByJobId(long jobId);

    /**
     * 检查任务状态
     */
    JobStatus checkJobStatus(long jobId);
}
