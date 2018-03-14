package com.iscreate.rnointerfermatrix.mapper.oracle;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.iscreate.rnointerfermatrix.model.Area;
import com.iscreate.rnointerfermatrix.model.Cell;
import com.iscreate.rnointerfermatrix.model.JobProfile;
import com.iscreate.rnointerfermatrix.model.Report;
import com.iscreate.rnointerfermatrix.model.RnoDataCollectRec;

@Mapper
public interface OracleMapper {

	/**
	 * 获取用户可访问区域
	 * 
	 * @param account
	 * @return
	 */
	public List<Area> getSpecialLevalAreaByAccount(String account);

	/**
	 * 通过城市ID获取所有区域
	 * 
	 * @param cityId
	 * @return
	 */
	public List<Cell> queryAllCellsByCity(long cityId);

	/**
	 * 增加一条新的报告记录
	 * 
	 * @param report
	 * @return
	 */
	public int addReport(Report report);

	/**
	 * 增加一条新的上传记录
	 * 
	 * @param rnoDataCollectRec
	 */
	public int addStatus(RnoDataCollectRec rnoDataCollectRec);

	/**
	 * 更新上传状态
	 * 
	 * @param rnoDataCollectRec
	 * @return
	 */
	public int updateStatus(RnoDataCollectRec rnoDataCollectRec);

	/**
	 * 获取用户可访问的指定级别的区域
	 * 
	 * @param map
	 * @return
	 */
	public List<Area> getSpecialUpperAreas(Map<String, Object> map);

	/**
	 * 获取用户配置的默认城市id
	 * 
	 * @param account
	 * @return
	 */
	public long getUserConfigAreaId(String account);

	/**
	 * 通过城市ID获取父ID即省
	 * 
	 * @param cfgCityId
	 * @return
	 */
	public long getParentIdByCityId(long cfgCityId);

	/**
	 * 查询符合条件的上传记录
	 * 
	 * @param map
	 * @return
	 */
	public int queryUploadDataCnt(Map<String, Object> map);

	/**
	 * 分页查询符合条件的上传记录
	 * 
	 * @param map
	 * @return
	 */
	public List<RnoDataCollectRec> queryUploadDataByPage(Map<String, Object> map);

	/**
	 * 查询某job的报告数量
	 * 
	 * @param jobId
	 * @return
	 */
	public int queryJobReportCnt(Long jobId);

	/**
	 * 分页查询某job的报告
	 * 
	 * @param map
	 * @return
	 */
	public List<Map<String, Object>> queryJobReportByPage(Map<String, Object> map);

	/**
	 * 添加一条任务信息
	 * 
	 * @param job
	 */
	public void addJob(JobProfile job);

	/**
	 * 更新任务开始时间
	 * 
	 * @param job
	 */
	public void updateJobBegTime(JobProfile job);

	/**
	 * 更新任务结束时间
	 * 
	 * @param job
	 */
	public void updateJobEndTime(JobProfile job);

	/**
	 * 更新任务运行状态
	 * 
	 * @param job
	 */
	public void updateJobRunningStatus(JobProfile job);

	/**
	 * 获取相应状态下的任务ID
	 * 
	 * @param job
	 * @return
	 */
	public String getJoBId(JobProfile job);

	/**
	 * 
	 * 获取符合条件的4g干扰矩阵数量
	 * 
	 * @param condition
	 * @return
	 */
	public long getLteInterferMartixCount(Map<String, Object> map);

	/**
	 * 
	 * @title 分页获取符合条件的4g干扰矩阵的详情
	 * @param map
	 */
	public List<Map<String, Object>> queryLteInterferMartixByPage(Map<String, Object> map);

	/**
	 * 检查这周是否计算过4G MR干扰矩阵
	 * 
	 * @return
	 */
	public List<Map<String, Object>> checkLteInterMartixThisWeek(Map<String, Object> map);

	/**
	 * 根据城市ID获取任务名列表
	 * 
	 * @param cityId
	 * @return
	 */
	public List<Map<String, String>> getTaskNameListByCityId(long cityId);

	/**
	 * @title 创建MR 4g 干扰矩阵计算任务
	 * @param map
	 */
	public int createLteInterMartixRec(Map<String, Object> map);

	/**
	 * @title 通过jobId获取4g 干扰矩阵的记录任务信息
	 * @param jobId
	 * @return
	 */
	public List<Map<String, Object>> query4GInterferMatrixRecByJobId(long jobId);

	/**
	 * @title 更新4g干扰矩阵的记录状态
	 * @param map
	 * @return
	 */
	public boolean update4GInterMatrixWorkStatus(Map<String, Object> map);

	/**
	 * 查询扫频文件名
	 * 
	 * @param map
	 */
	public List<Map<String, Object>> getSfFileName(Map<String, Object> map);

	/**
	 * 通过城市ID获取小区和经纬度的映射集合
	 */
	public List<Map<String, Object>> getParameterForCellsMap(long cityId);

	/**
	 * 通过主进程 jobId 更新 lte_pci_job 表中 MR_JOB_ID
	 */
	public void addMapReduceJobId(Map<String, Object> map);

	/**
	 * 查询干扰矩阵任务信息
	 */
	public List<Map<String, Object>> queryTaskInfo(long jobId);
}
