package com.iscreate.rno.microservice.pci.afp.service;

import java.util.List;
import java.util.Map;

import com.iscreate.rno.microservice.pci.afp.model.Area;

public interface RnoCommonService {

	/**
	 * 获取用户可访问的指定级别的区域
	 * 
	 * @param account
	 * @return
	 */
	public Map<String, List<Area>> getAreaByAccount(String account);

	/**
	 * 查询符合条件的上传记录
	 * 
	 * @param map
	 * @return
	 */
	public int queryUploadDataCnt(Map<String, Object> map);

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
