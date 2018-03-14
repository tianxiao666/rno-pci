package com.hgicreate.rno.dao;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import com.hgicreate.rno.model.G4SfDescQueryCond;

public interface Rno4GAzimuthCalcDao {



	/**
	 * 更新mr任务的id
	 * 
	 * @param jobId
	 * @param mrJobId
	 * @param type
	 */
	public void addMapReduceJobId(long jobId, String mrJobId, String type);

	/**
	 * 
	 * @title 通过区域ID获取LTE小区标识对应的小区信息
	 * @param cityId
	 * @return
	 */
	public Map<String, List<String>> getLteCellInfoByCellId(long cityId);
	
	
	/*
	 * 统计4g方位角任务数量
	 */
	public long get4GAzimuthCalcTaskCount(Map<String, String> cond, String account);
	/*
	 * 分页获取4g方位角计算任务信息
	 */
	public List<Map<String, Object>> query4GAzimuthCalcTaskByPage(Map<String, String> cond, String account,
			int startIndex, int cnt);
	/*
	 * 获取某个地市 的小区方位角信息
	 */
	public Map<String, String> queryCellAzimuthByCityId(long cityId);
	/*
	 * 获取某个地市 的小区频段类型信息
	 */
	public Map<String, String> queryCellBandTypeByCityId(long cityId);
	/**
	 * 从spark数据仓库获取网络覆盖所需的MR数据
	 * @param cityId
	 * @param startDate
	 * @param endDate
	 * @param connection
	 * @return
	 */
	public List<Map<String,Object>> queryNetWorkCoverDataFromSparkMrTable(
			long cityId,String startDate, String endDate, Connection connection,long jobId);
	/**
	 * 将mr网络覆盖结果批量插入oracle数据库
	 * @param mrDatas
	 */
	public boolean addNetworkCoverResBatch(List<Map<String,Object>>  mrDatas);
}
