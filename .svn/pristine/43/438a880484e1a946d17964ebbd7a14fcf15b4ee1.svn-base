package com.hgicreate.rno.mapper;

import java.util.List;
import java.util.Map;

import com.hgicreate.rno.model.Area;
import com.hgicreate.rno.model.JobProfile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.hgicreate.rno.model.Cell;
import com.hgicreate.rno.model.Report;

@Mapper
public interface G4AzimuthCalcMapper {

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
	public List<Report> queryJobReportByPage(Map<String, Object> map);

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
	 * 根据任务ID获取商家
	 * 
	 * @param jobId
	 * @return
	 */
	public String getManufacturerByJobId(long jobId);

	/**
	 * 根据城市ID获取城市名
	 * 
	 * @param cityId
	 * @return
	 */
	public String getCityNameByCityId(long cityId);


	/**
	 * 更新任务ID
	 */
	public int updateJobId(Map<String, Long> map);


	/**
	 * 通过 jobId 获取4g方位角计算记录信息
	 */
	public List<Map<String, Object>> query4GAzimuthJobRecByJobId(long jobId);

	/**
	 * 更新PCI规划的job状态
	 */
	public void updatePciPlanWorkStatus(Map<String, Object> map);

	/**
	 * 通过主进程 jobId 更新 rno_ms_4g_azimuth_job 表中 MR_JOB_ID
	 */
	public void addMapReduceJobId(Map<String, Object> map);

	/**
	 * 根据任务ID获取任务名称
	 */
	public JobProfile getJobNameByJobId(long jobId);

	/**
	 * 通过jobId获取干扰矩阵计算记录信息
	 */
	public List<Map<String, Object>> query4GInterferMatrixRecByJobId(long jobId);


	/**
	 * 更新任务名
	 */
	public void updateJobNameById(Map<String, Object> map);

	/**
	 * 通过任务ID获取任务信息
	 */
	public List<Map<String, Object>> getTaskInfoByJobId(long jobId);

	/**
	 * 通过城市ID获取从基站标识到lte小区的映射集合
	 */
	public List<Map<String, Object>> getLteCellInfoByCellId(long cityId);
	
	
	/* -----------------------开始---------------------------------  */
	/*
	 * 统计4g方位角任务数量
	 */
	public long get4GAzimuthCalcTaskCount(Map<String, String> cond);
	/*
	 * 分页获取4g方位角计算任务信息
	 */
	public List<Map<String, Object>> query4GAzimuthCalcTaskByPage(Map<String, String> cond);
	/*
	 * 保存4G方位角工作信息
	 */
	public void save4GAzimuthJob(Map<String, String> cond);
	/*
	 * 获取某个地市 的小区方位角信息
	 */
	public List<Map<String, Object>> queryCellAzimuthByAreaStr(@Param("areaIdStr")String areaIdStr);
	/*
	 * 获取某个地市 的小区频段类型信息
	 */
	public List<Map<String, Object>> queryCellBandTypeByAreaStr(@Param("areaIdStr")String areaIdStr);
	
	/*
	 * 获取所有区域集合信息
	 */
	public List<Map<String, Object>> getAllAreaList();

	/**
	 * 将mr网络覆盖结果批量插入oracle数据库
	 * @param mrDatas
	 */
	public void addNetworkCoverResBatch(List<Map<String,Object>>  mrDatas);

	/**
	 * 通过 jobId 获取网络覆盖计算记录信息
	 */
	public List<Map<String, Object>> queryNetWorkCoverResByJobId(long jobId);
}
