package com.hgicreate.rno.service;

import java.util.List;
import java.util.Map;

import com.hgicreate.rno.model.Area;

public interface RnoCommonService {

	/**
	 * 获取用户可访问的指定级别的区域
	 * 
	 * @param account
	 * @return
	 */
	public Map<String, List<Area>> getAreaByAccount(String account);

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
