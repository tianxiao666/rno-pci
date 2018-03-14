package com.iscreate.rnointerfermatrix.dao;

import java.util.List;
import java.util.Map;

import com.iscreate.rnointerfermatrix.model.G4HoDescQueryCond;
import com.iscreate.rnointerfermatrix.model.G4MrDescQueryCond;
import com.iscreate.rnointerfermatrix.model.G4SfDescQueryCond;
import com.iscreate.rnointerfermatrix.model.Page;
import com.iscreate.rnointerfermatrix.model.RnoLteInterMatrixTaskInfo;

public interface RnoInterferMatrixDao {

	/**
	 * 
	 * @title 获取符合条件的4g干扰矩阵数量
	 * @param condition
	 * @return
	 */
	public long getLteInterferMartixCount(Map<String, Object> condition);

	/**
	 * 
	 * @title 分页获取符合条件的4g干扰矩阵的详情
	 * @param condition
	 * @param startIndex
	 * @param cnt
	 * @return
	 */
	public List<Map<String, Object>> queryLteInterferMartixByPage(Map<String, Object> map);

	/**
	 * 根据城市ID获取任务名列表
	 * 
	 * @param cityId
	 * @return
	 */
	public List<Map<String, String>> getTaskNameListByCityId(long cityId);

	/**
	 * 
	 * @title 检查这周是否计算过4G MR干扰矩阵
	 * @param cityId
	 * @param thisMonday
	 * @param today
	 * @return
	 */
	public List<Map<String, Object>> checkLteInterMartixThisWeek(long cityId, String thisMonday, String today);

	/**
	 * 
	 * @title 创建MR 4g 干扰矩阵计算任务
	 * @param interMartix
	 * @return
	 */
	public boolean createLteInterMartixRec(long jobId, RnoLteInterMatrixTaskInfo taskInfo);

	/**
	 * 通过城市ID获取小区和经纬度的映射集合
	 * 
	 * @param cityId
	 * @return
	 */
	public Map<String, Object> getParameterForCellsMap(long cityId);

	/**
	 * 更新干扰矩阵任务的id
	 * 
	 * @param jobId
	 * @param mrJobId
	 * @param type
	 */
	public void addMapReduceJobId(long jobId, String mrJobId, String type);

	public int queryMrDataRecordsCount(int cityId, String begTime, String endTime);

	public int queryHoDataRecordsCount(int cityId, String begTime, String endTime);
	
	/**
	 * 
	 * @title 分页获取Hbase的Mr数据描述表的数据
	 * @param cond
	 * @param newPage
	 * @return
	 */
	public List<Map<String, Object>> queryMrDataFromHbaseByPage(G4MrDescQueryCond cond, Page newPage);
	
	/**
	 * 
	 * @title 分页获取Hbase的Ho数据描述表的数据
	 * @param cond
	 * @param newPage
	 * @return
	 */
	public List<Map<String, Object>> queryHoDataFromHbaseByPage(G4HoDescQueryCond cond, Page newPage);
	
	/**
	 * 
	 * @title 分页获取Hbase的Sf数据描述表的数据
	 * @param cond
	 * @param newPage
	 * @return
	 */
	public List<Map<String, Object>> querySfDataFromHbaseByPage(G4SfDescQueryCond cond, Page newPage);

}
