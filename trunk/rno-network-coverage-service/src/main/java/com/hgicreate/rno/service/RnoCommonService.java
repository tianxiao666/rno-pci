package com.hgicreate.rno.service;

import java.util.List;
import java.util.Map;

import com.hgicreate.rno.model.Area;
import com.hgicreate.rno.model.Report;

public interface RnoCommonService {

	/**
	 * 获取用户可访问的指定级别的区域
	 * 
	 * @param account
	 * @return
	 */
	public Map<String, List<Area>> getAreaByAccount(String account);


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
	 * 根据父区域，获取用户可访问的指定级别的子区域
	 * 
	 * @param account
	 * @param parentAreaId
	 * @param subAreaLevel
	 * @return
	 */
	public List<Area> getSpecialSubAreasByAccountAndParentArea(String account, long parentAreaId, String subAreaLevel);
}
