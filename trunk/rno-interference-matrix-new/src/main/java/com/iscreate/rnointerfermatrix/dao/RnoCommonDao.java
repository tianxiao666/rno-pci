package com.iscreate.rnointerfermatrix.dao;

import java.util.List;
import java.util.Map;

import com.iscreate.rnointerfermatrix.model.Area;
import com.iscreate.rnointerfermatrix.model.RnoDataCollectRec;

public interface RnoCommonDao {

	/**
	 * 获取用户可访问的指定级别的区域
	 * 
	 * @param account
	 * @return
	 */
	public List<Area> getSpecialLevalAreaByAccount(String account);

	/**
	 * 获取指定区域集合的所有上级区域
	 * 
	 * @param area_ids
	 * @param areaLevel
	 * @return
	 */
	public List<Area> getSpecialUpperAreas(long[] area_ids, String areaLevel);

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

}
