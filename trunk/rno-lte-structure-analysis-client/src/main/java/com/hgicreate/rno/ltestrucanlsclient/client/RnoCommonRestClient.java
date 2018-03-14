package com.hgicreate.rno.ltestrucanlsclient.client;

import com.hgicreate.rno.ltestrucanlsclient.model.Area;
import com.hgicreate.rno.ltestrucanlsclient.model.JobProfile;
import com.hgicreate.rno.ltestrucanlsclient.model.Report;

import java.util.List;
import java.util.Map;

public interface RnoCommonRestClient {
    /**
     * 验证用户身份
     */
    Boolean verifyUserIdentity(String account);

    /**
     * 根据用户名和指定城市ID获取允许访问的区域
     */
    Map<String, List<Area>> getAreaByAccount(String account, long currentCityId);

    /**
     * 根据父区域ID，获取指定类型的子区域列表
     */
    List<Area> getSubAreaByParent(String account, long parentAreaId, String subAreaLevel);

    /**
     * 查询某job的报告数量
     */
    Long queryReportCnt(long jobId);

    /**
     * 分页查询某job的报告
     */
    List<Report> queryReportByPage(long jobId, int start, int end);

    /**
     * 添加一条任务信息
     */
    Long addOneJob(JobProfile job);

    /**
     * 终止任务
     */
    void stopJobByJobId(Long jobId);
}
