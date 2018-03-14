package com.iscreate.rno.microservice.pci.afp.dao;

import java.util.List;
import java.util.Map;

import com.iscreate.rno.microservice.pci.afp.model.G4SfDescQueryCond;
import com.iscreate.rno.microservice.pci.afp.model.RnoThreshold;
import com.iscreate.rno.microservice.pci.afp.model.TaskCond;
import com.iscreate.rno.microservice.pci.afp.model.RnoLteInterferCalcTask.TaskInfo;

public interface RnoPciAfpDao {

	/**
	 * 
	 * @title 通过区域及起始时间和厂家,及数据类型从Hbase获取MR数据描述记录情况
	 * @param cityId
	 * @param startTime
	 * @param endTime
	 * @param factory
	 *            ERI,ZTE
	 * @param dataType
	 *            HO,MR
	 * @return
	 */
	public List<Map<String, Object>> getDataDescRecordFromHbase(long cityId, String startTime, String endTime,
			String factory, String dataType);

	/**
	 * 分页获取扫频数据
	 * 
	 * @param cond
	 * @return
	 */
	public List<Map<String, String>> querySfDataFromHbaseByPage(G4SfDescQueryCond cond);

	/**
	 * 统计pci自动规划任务数量
	 * 
	 * @param cond
	 * @param account
	 * @return
	 */
	public long getPciAnalysisTaskCount(TaskCond cond);

	/**
	 * 分页获取pci自动规划任务信息
	 * 
	 * @param cond
	 * @param account
	 * @param startIndex
	 * @param cnt
	 * @return
	 */
	public List<Map<String, Object>> queryPciPlanTaskByPage(TaskCond cond);

	/**
	 * 保存pci计算任务信息
	 * 
	 * @param jobId
	 * @param rnoThresholds
	 * @param taskInfo
	 * @return
	 */
	public Map<String, Object> savePciPlanAnalysisTaskInfo(long jobId, List<RnoThreshold> rnoThresholds,
			TaskInfo taskInfo);

	/**
	 * 通过城市ID获取小区和经纬度的映射集合
	 * 
	 * @param cityId
	 * @return
	 */
	public Map<String, Object> getParameterForCellsMap(long cityId);

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
}
