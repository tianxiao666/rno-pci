package com.hgicreate.rno.dao;

import java.util.List;
import java.util.Map;

public interface ServiceForPciEvalDao {

	/**
	 * 获取用户可访问的指定级别的区域
	 * 
	 * @param account
	 * @return
	 */
	public List<Map<String, Object>> getSpecialLevalAreaByAccount(String account);

	/**
	 * 获取指定区域集合的所有上级区域
	 * 
	 * @param area_ids
	 * @param areaLevel
	 * @return
	 */
	public List<Map<String, Object>> getSpecialUpperAreas(long[] area_ids, String areaLevel);

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
	 * 获取同站lte小区和pci
	 */
	public List<Map<String, String>> getSameStationCellsByLteCellId(String lteCell);
	
	/**
	 * 转换lte小区与某同站小区的pci
	 */
	public boolean changeLteCellPci(final String cell1, final String pci1, final String cell2, final String pci2);
}
