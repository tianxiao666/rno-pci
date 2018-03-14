package com.hgicreate.rno.service;

import com.hgicreate.rno.model.*;
import com.hgicreate.rno.web.rest.vm.AzimuthEvaluationSubmitTaskVm;
import com.hgicreate.rno.web.rest.vm.TaskQueryVm;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

/**
 * 方位角评估服务接口
 *
 * @author li.tf
 * @date 2018-01-12 14:22:23
 */
public interface AzimuthEvalService {

    /**
     * 获取最新测量时间
     *
     * @return 最新时间
     * @date 2018-01-12 14:22:39
     */
    Map<String, Object> getNewestTime();

    /**
     * 计算方位角评估
     *
     * @param map 计算条件
     * @return 方位角评估结果集
     * @date 2018-01-12 14:22:58
     */
    List<AzimuthEvalResult> calcAzimuth(Map<String, Object> map);

    /**
     * 获取评估结果
     *
     * @param jobId 任务ID
     * @return 方位角评估结果集
     * @date 2018-01-12 14:23:20
     */
    List<Map<String, Object>> findAzimuthEvalResultsByJobId(long jobId);

    /**
     * 获取1000条评估结果（用于页面展示）
     *
     * @param jobId 任务ID
     * @return 1000条评估结果
     * @date 2018-01-12 14:23:49
     */
    List<Map<String, Object>> findTop1000AzimuthEvalResultsByJobId(long jobId);

    /**
     * 保存评估结果
     *
     * @param azimuthEvalResults 评估结果
     * @return 评估结果
     * @date 2018-01-12:23:59
     */
    List<AzimuthEvalResult> saveAzimuthEvalResults(List<AzimuthEvalResult> azimuthEvalResults);

    /**
     * 查询任务记录
     *
     * @param cond 查询条件
     * @return 任务记录集
     * @date 2018-01-12 14:24:02
     */
    List<AzimuthEvalTask> findAllTask(TaskQueryVm cond);

    /**
     * 通过任务ID获取任务记录
     *
     * @param jobId 任务ID
     * @return 任务记录
     * @date 2018-01-12 14:24:17
     */
    AzimuthEvalTask getOneAzimuthEvalTask(long jobId);

    /**
     * 提交任务
     *
     * @param cond 表单数据
     * @return 提交结果
     * @date 2018-01-12 14:24:33
     */
    SubmitResult submitTask(AzimuthEvaluationSubmitTaskVm cond);

    /**
     * 更新任务状态
     *
     * @param jobId     任务ID
     * @param jobStatus 任务状态
     * @return 更新结果
     * @date 2018-01-12 14:25:10
     */
    boolean updateTaskStatus(long jobId, String jobStatus);

    /**
     * 更新任务测量时间
     *
     * @param jobId 任务ID
     * @param date  测量时间
     * @return 更新结果
     * @date 2018-01-12 14:25:49
     */
    boolean updateTaskMeaTime(long jobId, String date);

    /**
     * 添加报告到数据库
     *
     * @param jobId  任务ID
     * @param date1  开始时间
     * @param msg    报告
     * @param status 状态
     * @param stage  阶段
     * @date 2018-01-12 14:26:20
     */
    void addOneReport(long jobId, Timestamp date1, String msg, String status, String stage);

    /**
     * 开始任务，更新记录
     *
     * @param job 任务对象
     * @date 2018-01-12 14:26:49
     */
    void startJob(Job job);

    /**
     * 结束任务，更新记录
     *
     * @param jobId     任务ID
     * @param startTime 开始时间
     * @param jobStatus 任务状态
     * @date 2018-01-12 14:27:01
     */
    void endJob(long jobId, Timestamp startTime, String jobStatus);

    /**
     * 判断任务是否结束
     *
     * @param jobId 任务记录
     * @return 判断结果
     * @date 2018-01-12 14:27:30
     */
    boolean isJobStopped(long jobId);

    /**
     * 通过任务状态，类型获取任务记录
     *
     * @param job 任务对象
     * @return 任务对象
     * @date 2018-01-12 14:27:39
     */
    Job getOneJob(Job job);

    /**
     * 通过jobId获取任务记录
     *
     * @param jobId 任务ID
     * @return 任务对象
     * @date 2018-01-12 14:27:49
     */
    Job getJobByJobId(long jobId);

    /**
     * 更新任务自身状态
     *
     * @param jobId     任务ID
     * @param jobStatus 任务状态
     * @date 2018-01-12 14:28:10
     */
    void updateOwnProgress(long jobId, String jobStatus);

    /**
     * 更新任务测量时间
     *
     * @param jobId 任务ID
     * @param date  测量时间
     * @date 2018-01-12 14:28:39
     */
    void updateMeatime(long jobId, String date);

    /**
     * 通过 jobId 获取Lte方位角计算记录信息
     *
     * @param jobId 任务ID
     * @return 任务对象
     * @date 2018-01-12 14:28:37
     */
    AzimuthEvalTask queryTaskRecordByJobId(long jobId);

    /**
     * 批量入库
     *
     * @param results 方位角评估结果集
     * @return 入库结果数
     * @date 2018-01-12 14:28:50
     */
    Long batchInsertResult(List<AzimuthEvalResult> results);

    /**
     * 通过任务ID获取方位角评估结果
     *
     * @param jobId 任务时间
     * @return 方位角评估结果集
     * @date 2018-01-12 14:29:19
     */
    List<Map<String, Object>> queryResultByJobId(long jobId);

    /**
     * 通过任务ID获取增强方案
     *
     * @param jobId 任务ID
     * @return 增强方案结果集
     * @date 2018-01-12 14:29:30
     */
    List<Map<String, Object>> queryEnhanceByJobId(long jobId);

}
