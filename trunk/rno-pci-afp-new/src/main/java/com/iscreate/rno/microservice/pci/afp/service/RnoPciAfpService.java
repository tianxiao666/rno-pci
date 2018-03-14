package com.iscreate.rno.microservice.pci.afp.service;

import java.util.List;
import java.util.Map;

import com.iscreate.rno.microservice.pci.afp.model.G4SfDescQueryCond;
import com.iscreate.rno.microservice.pci.afp.model.Page;
import com.iscreate.rno.microservice.pci.afp.model.RnoLteInterferCalcTask;
import com.iscreate.rno.microservice.pci.afp.model.RnoThreshold;
import com.iscreate.rno.microservice.pci.afp.model.TaskCond;

public interface RnoPciAfpService {
	
	/**
	 * 获取最近十次 LTE 干扰矩阵信息
	 */
	public List<Map<String, Object>> getLatelyLteMatrixByCityId(long cityId);

	/**
	 * 通过模块类型获取阈值门限对象集合
	 * 
	 * @param moduleType
	 * @return
	 */
	public List<RnoThreshold> getThresholdsByModuleType(String moduleType);

	/**
	 * 
	 * @title 通过区域及起始时间和厂家,及数据类型从Hbase获取MR数据描述记录情况
	 * @param cityId
	 * @param startTime
	 * @param endTime
	 * @param factory
	 *            ERI,ZTE
	 * @return
	 */
	public List<Map<String, Object>> getDataRecordFromHbase(long cityId, String startTime, String endTime,
			String factory);

	/**
	 * 分页获取扫频数据
	 * 
	 * @param cond
	 * @return
	 */
	public List<Map<String, String>> querySfDataFromHbaseByPage(G4SfDescQueryCond cond);

	/**
	 * 查询pci自动规划任务
	 * 
	 * @param cond
	 * @param newPage
	 * @param account
	 * @return
	 */
	//public List<Map<String, Object>> queryPciPlanTaskByPage(Map<String, String> cond, Page newPage);
	public List<Map<String, Object>> queryPciPlanTaskByPage(TaskCond cond, Page newPage);

	/**
	 * 通过任务id停止任务
	 * 
	 * @param jobId
	 * @return
	 */
	public boolean stopJob(long jobId);

	/**
	 * 提交PCI规划任务
	 * 
	 * @param jobId
	 * @param account
	 * @param threshold
	 * @param taskInfo
	 * @return
	 */
	public Map<String, Object> submitPciPlanAnalysisTask(long jobId, String account, List<RnoThreshold> rnoThresholds,
			RnoLteInterferCalcTask.TaskInfo taskInfo);

	/**
	 * 
	 * @title 通过区域ID获取LTE小区标识对应的小区信息
	 * @param cityId
	 * @return
	 */
	public Map<String, List<String>> getLteCellInfoByCellId(long cityId);
}
