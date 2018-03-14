package com.hgicreate.rno.service;

import java.util.List;
import java.util.Map;

public interface ServiceForPciEvalService {

	/**
	 * 获取用户可访问的指定级别的区域
	 */
	public Map<String, List<Map<String, Object>>> getAreaByAccount(String account);
	
	/**
	 * 根据父区域，获取用户可访问的指定级别的子区域
	 */
	public List<Map<String, Object>> getSpecialSubAreasByAccountAndParentArea(String account, long parentAreaId, String subAreaLevel);	
	
	/**
	 * 获取最近十次 LTE 干扰矩阵信息
	 */
	public List<Map<String, Object>> getLatelyLteMatrixByCityId(long cityId);
	
	/**
	 * 获取同站lte小区和pci
	 */
	public List<Map<String, String>> getSameStationCellsByLteCellId(String lteCell);
	
	/**
	 * 转换lte小区与某同站小区的pci
	 */
	public Map<String, Boolean> changeLteCellPci(String cell1, String pci1, String cell2, String pci2);
	
	/**
	 * 获取IN干扰小区信息
	 */
	public List<Map<String, Object>> getInCellInfo(int jobId, String cellId);
	
	/**
	 * 获取OUT干扰小区信息
	 */
	public List<Map<String, Object>> getOutCellInfo(int jobId, String ncellId);
	
	/**
	 * 获取PCI智能优化小区信息
	 */
	public List<Map<String, Object>> getPciCellInfo(int jobId, String cellId);
	
}
