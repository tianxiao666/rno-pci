package com.hgicreate.rno.service;

import java.util.List;
import java.util.Map;

import com.hgicreate.rno.model.G4SfDescQueryCond;
import com.hgicreate.rno.model.MrJobCond;
import com.hgicreate.rno.model.Page;

public interface Rno4GAzimuthCalcService {


	/**
	 * 通过任务id和mapreduce任务id停止任务
	 * 
	 * @param mrJobCond
	 * @return
	 */
	public boolean stopJobByJobIdAndMrJobId(MrJobCond mrJobCond);


	/**
	 * 
	 * @title 通过区域ID获取LTE小区标识对应的小区信息
	 * @param cityId
	 * @return
	 */
	public Map<String, List<String>> getLteCellInfoByCellId(long cityId);
	
	/*
	 * 查询4g方位角计算任务
	 */
	public List<Map<String, Object>> query4GAzimuthCalcTaskByPage(
			Map<String, String> cond, Page page, String account);
	/*
	 * 提交4g方位角计算任务
	 */
	public Map<String, Object> submit4GAzimuthCalcTask(String account,
			final Map<String, Object> taskInfo);
	/**
	 * 通过 jobId 获取4g方位角计算记录信息
	 */
	public List<Map<String, Object>> query4GAzimuthJobRecByJobId(long jobId);
	/**
	 * 通过 jobId 获取网络覆盖计算记录信息
	 */
	public List<Map<String, Object>> queryNetWorkCoverResByJobId(long jobId);
}
