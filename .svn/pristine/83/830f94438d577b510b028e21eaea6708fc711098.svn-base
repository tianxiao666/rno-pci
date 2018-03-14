package com.iscreate.rnointerfermatrix.service;

import java.util.List;
import java.util.Map;

import com.iscreate.rnointerfermatrix.model.G4HoDescQueryCond;
import com.iscreate.rnointerfermatrix.model.G4MrDescQueryCond;
import com.iscreate.rnointerfermatrix.model.G4SfDescQueryCond;
import com.iscreate.rnointerfermatrix.model.Page;
import com.iscreate.rnointerfermatrix.model.RnoLteInterMatrixTaskInfo;

public interface RnoInterferMatrixService {

	/**
	 * 
	 * @title 分页查询4g干扰矩阵信息
	 * @param condition
	 * @param page
	 * @return
	 */
	public List<Map<String, Object>> queryLteInterferMartixByPage(Map<String, Object> condition, Page page);

	/**
	 * 
	 * @title 检查这周是否计算过4g pci干扰矩阵
	 * @param areaId
	 * @param thisMonday
	 * @param today
	 * @return
	 */
	public List<Map<String, Object>> checkLteInterMartixThisWeek(long areaId, String thisMonday, String today);

	/**
	 * 
	 * @title 获取任务名列表
	 * @param attachParams
	 * @return
	 */
	public List<String> queryTaskNameListByCityId(long cityId);


	/**
	 * @title 新增4g pci 干扰矩阵
	 * @param taskInfo
	 */
	public boolean addLteInterMartix(RnoLteInterMatrixTaskInfo taskInfo);

	public List<Map<String, Object>> queryMrDataFromHbaseByPage(G4MrDescQueryCond g4MrDescQueryCond, Page newPage);

	public List<Map<String, Object>> queryHoDataFromHbaseByPage(G4HoDescQueryCond g4HoDescQueryCond, Page newPage);

	public List<Map<String, Object>> querySfDataFromHbaseByPage(G4SfDescQueryCond g4SfDescQueryCond, Page newPage);

	public int queryMrDataCountByCond(RnoLteInterMatrixTaskInfo taskInfo);

	public int queryHoDataCountByCond(RnoLteInterMatrixTaskInfo taskInfo);

}
