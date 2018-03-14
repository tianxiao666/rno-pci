package com.hgicreate.rno.ltestrucanlsservice.service;

import com.hgicreate.rno.ltestrucanlsservice.model.Area;
import com.hgicreate.rno.ltestrucanlsservice.model.JobProfile;
import com.hgicreate.rno.ltestrucanlsservice.model.JobStatus;
import com.hgicreate.rno.ltestrucanlsservice.model.Report;

import java.util.List;
import java.util.Map;

public interface RnoCommonService {
    /**
     * 验证用户身份
     */
    Boolean verifyUserIdentity(String account);
    /**
     * 根据用户名和指定城市ID获取允许访问的区域
     */
    Map<String, List<Area>> getAreaByAccount(String account, long currentCityId);

    /**
     * 根据父区域，获取用户可访问的指定级别的子区域
     */
    List<Area> getSubAreaByParent(String account, long parentId, String areaLevel);

    /**
     * 获取用户可访问的指定级别的区域
     */
    List<Area> getSpecialAreaByAccount(String account, String areaLevel);

    /**
     * 增加一条新的报告记录
     */
    Long addOneReport(Report report);

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
