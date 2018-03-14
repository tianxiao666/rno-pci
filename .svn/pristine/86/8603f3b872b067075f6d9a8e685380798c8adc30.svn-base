package com.iscreate.rno.microservice.pci.afp.mapper.oracle;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.iscreate.rno.microservice.pci.afp.model.Area;
import com.iscreate.rno.microservice.pci.afp.model.Cell;
import com.iscreate.rno.microservice.pci.afp.model.JobProfile;
import com.iscreate.rno.microservice.pci.afp.model.Report;
import com.iscreate.rno.microservice.pci.afp.model.RnoDataCollectRec;
import com.iscreate.rno.microservice.pci.afp.model.RnoThreshold;
import com.iscreate.rno.microservice.pci.afp.model.TaskCond;

@Mapper
public interface OracleMapper {
	
	/**
	 * 获取最近十次lte干扰矩阵信息
	 */
	public List<Map<String, Object>> getLatelyLteMatrixByCityId(long cityId);

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
	 * 根据任务ID获取商家
	 * 
	 * @param jobId
	 * @return
	 */
	public String getManufacturerByJobId(long jobId);

	/**
	 * 通过模块类型获取阈值门限对象集合
	 * 
	 * @param moduleType
	 * @return
	 */
	public List<RnoThreshold> getThresholdsByModuleType(String moduleType);

	/**
	 * 统计pci自动规划任务数量
	 * 
	 * @param cond
	 * @return
	 */
	public long getPciAnalysisTaskCount(TaskCond cond);

	/**
	 * 分页获取pci自动规划任务信息
	 * 
	 * @param cond
	 * @return
	 */
	public List<Map<String, Object>> queryPciPlanTaskByPage(TaskCond cond);

	/**
	 * 根据记录ID获取任务ID
	 */
	public long getJobIdFromDataCollectById(long dataCollectId);

	/**
	 * 更新任务ID
	 */
	public int updateJobId(Map<String, Long> map);

	/**
	 * 保存门限值
	 */
	public int saveJobParam(Map<String, Object> map);

	/**
	 * 保存PCI任务信息
	 */
	public int savePciTaskInfo(Map<String, Object> map);

	/**
	 * 通过 jobId 获取干扰矩阵计算记录信息
	 */
	public List<Map<String, Object>> queryPciPlanJobRecByJobId(long jobId);

	/**
	 * 通过城市ID获取小区和经纬度的映射集合
	 */
	public List<Map<String, Object>> getParameterForCellsMap(long cityId);

	/**
	 * 通过城市ID获取扫频文件信息
	 */
	public List<Map<String, Object>> getSfFileInfo(Map<String, Object> map);

	/**
	 * 通过jobId获取页面自定义的阈值门限值
	 */
	public List<Map<String, Object>> queryParamInfo(long jobId);

	/**
	 * 获取默认门限值
	 */
	public List<Map<String, Object>> queryDefaultParamInfo(long jobId);

	/**
	 * 获取流量文件信息
	 */
	public List<Map<String, Object>> getFlowFileInfoAsMap(long flowDcId);

	/**
	 * 获取流量文件信息
	 */
	public RnoDataCollectRec getFlowFileInfoAsBean(long flowDcId);

	/**
	 * 获取干扰矩阵导入计算信息
	 */
	public List<Map<String, Object>> getmatrixInfo(long matrixDcId);

	/**
	 * 更新PCI规划的job状态
	 */
	public void updatePciPlanWorkStatus(Map<String, Object> map);

	/**
	 * 通过主进程 jobId 更新 lte_pci_job 表中 MR_JOB_ID
	 */
	public void addMapReduceJobId(Map<String, Object> map);

	/**
	 * 查询流量数据信息
	 */
	public List<Map<String, String>> queryFlowInfo();

	/**
	 * 查询道路小区信息
	 */
	public List<Map<String, String>> queryDtInfo();

	/**
	 * 清空流量临时数据表
	 */
	public void deleteFlowTab();

	/**
	 * 插入数据到流量临时表
	 */
	public void insertToFlowTab(List<Map<String, Object>> list);

	/**
	 * 根据任务ID获取任务名称
	 */
	public JobProfile getJobNameByJobId(long jobId);

	/**
	 * 通过jobId获取干扰矩阵计算记录信息
	 */
	public List<Map<String, Object>> query4GInterferMatrixRecByJobId(long jobId);

	/**
	 * 更新pci优化干扰矩阵状态
	 */
	public void update4GInterMatrixWorkStatus(Map<String, Object> map);

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

	/**
	 * 更新jobId
	 */
	public void updateJobIdWithDataId(Map<String, Object> map);
	
	/**
	 * 保存最佳方案
	 */
	public int saveBestPlan(List<Map<String, Object>> list);
	
	/**
	 * 获取最佳方案
	 */
	public List<Map<String, Object>> getBestPlan(long jobId);
	
	/**
	 * 保存中间方案
	 */
	public int saveMidPlan(List<Map<String, Object>> list);
	
	/**
	 * 获取中间方案个数
	 */
	public int getMidPlanCount(long jobId);
	
	/**
	 * 根据方案号获取中间方案
	 */
	public List<Map<String, Object>> getMidPlanByPlanNum(@Param("jobId")long jobId,@Param("planNum")int planNum);
	
	/**
	 * 保存topCell
	 */
	public int saveTopCell(List<Map<String, Object>> list);
	
	/**
	 * 获取topCell
	 */
	public List<Map<String, Object>> getTopCell(@Param("jobId")long jobId,@Param("planNum")int planNum);
	
	/**
	 * 保存邻区核查方案
	 */
	public int saveNcCheckPlan(List<Map<String, Object>> list);
	
	/**
	 * 获取邻区核查方案
	 */
	public List<Map<String, Object>> getNcCheckPlan(long jobId);
	
	/**
	 * 保存关联表数据
	 */
	public int saveAssoTable(List<Map<String, Object>> list);
	
	/**
	 * 获取关联表数据
	 */
	public List<Map<String, Object>> getAssoTable(long jobId);
	
	/**
	 * 保存d1小区表
	 */
	public int saveD1Cell(List<Map<String, Object>> list);
	
	/**
	 * 获取d1小区表
	 */
	public List<Map<String, Object>> getD1Cell(long jobId);
	
	/**
	 * 保存d2小区表
	 */
	public int saveD2Cell(List<Map<String, Object>> list);
	
	/**
	 * 获取d2小区表
	 */
	public List<Map<String, Object>> getD2Cell(long jobId);
	
	/**
	 * 停止任务
	 */
	public int stopJob(@Param("jobId")long jobId, @Param("now")String now);

}
