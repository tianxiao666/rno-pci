package com.hgicreate.rno.mapper.oracle;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface OracleMapper {

	/**
	 * 获取用户可访问区域
	 */
	public List<Map<String, Object>> getSpecialLevalAreaByAccount(String account);
	
	/**
	 * 获取用户可访问的指定级别的区域
	 */
	public List<Map<String, Object>> getSpecialUpperAreas(Map<String, Object> map);

	/**
	 * 获取用户配置的默认城市id
	 */
	public long getUserConfigAreaId(String account);

	/**
	 * 通过城市ID获取父ID即省
	 */
	public long getParentIdByCityId(long cfgCityId);
	
	/**
	 * 获取最近十次lte干扰矩阵信息
	 */
	public List<Map<String, Object>> getLatelyLteMatrixByCityId(long cityId);
	
	/**
	 * 获取服务小区信息
	 */
	public List<Map<String, Object>> getServerCellDetailByCellId(@Param("lteCell") String lteCell);
	
	/**
	 * 获取同站小区信息
	 */
	public List<Map<String, Object>> getSameStationCellDetailByCellId(Map<String, String> cond);
	
	/**
	 * 转换lte小区与某同站小区的pci
	 */
	public int changeLteCellPci(@Param("pci")String pci,@Param("cell")String cell);
}
