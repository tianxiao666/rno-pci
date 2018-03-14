package com.hgicreate.rno.mapper;

import java.util.List;
import java.util.Map;

import com.hgicreate.rno.model.Cell;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.hgicreate.rno.model.Area;

@Mapper
public interface LteDynaOverGraphMapper {

	/**
	 * 获取用户可访问区域 ok
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
	 * 获取用户可访问的指定级别的区域 ok
	 * 
	 * @param map
	 * @return
	 */
	public List<Area> getSpecialUpperAreas(Map<String, Object> map);

	/**
	 * 获取用户配置的默认城市id ok
	 * 
	 * @param account
	 * @return
	 */
	public String getUserConfigAreaId(String account);

	/**
	 * 通过城市ID获取父ID即省 ok
	 * 
	 * @param cfgCityId
	 * @return
	 */
	public long getParentIdByCityId(long cfgCityId);
	/**
	 * 
	* @Title: 获取指定区域下的指定类型的子区域 ok
	* @Description: 
	* @Company:  怡创科技
	* @param area_ids
	* @param area_level
	* @return
	* @return: List<Map<String,Object>>
	* @author chao_xj
	* @date 2016年6月3日
	 */
	public List<Map<String,Object>> getSubAreasInSpecAreaLevel(@Param("area_ids") long[] area_ids, @Param("area_level") String area_level);
	/**
	 * 
	* @Title: 根据人员账号获取人员对应区域
	* @Description: 
	* @Company:  怡创科技
	* @param account
	* @return
	* @return: List<Map<String,Object>>
	* @author chao_xj
	* @date 2016年6月3日
	 */
	public List<Map<String,Object>> getAreaByAccount(String account);
	/**
	 * 
	* @Title: 通过区域ID获取指定区域的地图经纬度纠偏集合
	* @Description: 
	* @Company:  怡创科技
	* @param areaid
	* @param mapType
	* @return
	* @return: List<RnoMapLnglatRelaGps>
	* @author chao_xj
	* @date 2016年6月3日
	 */
//	public List<RnoMapLnglatRelaGps> getSpecialAreaRnoMapLnglatRelaGpsList(@Param("areaid") long areaid, @Param("mapType") String mapType);
	/**
	 * 
	* @Title: 获取所有区域集合信息
	* @Description: 
	* @Company:  怡创科技
	* @return
	* @return: List<Map<String,Object>>
	* @author chao_xj
	* @date 2016年6月4日
	 */
	public List<Map<String, Object>> getAllAreaList();

	/**
	 * 通过cityId获取E频段的lte小区集
	 */
//	public List<Map<String, Object>> queryLteCellWhichBandIsEByCityId(long cityId);
	/**
	 * 获取某个地市 的小区频段类型信息
	 */
//	public List<Map<String, Object>> queryCellBandTypeByAreaStr(@Param("areaStr") String areaStr);
	/*
	 * 获取lte小区形状数据通过区域 即三角形的三点坐标
	 */
	public List<Map<String, Object>> queryLteCellShapeDataByAreaStr(@Param("areaStr") String areaStr);
	/**
	 * 通过cityId获取站间距的lte小区集
	 */
//	public List<Map<String, Object>> queryCellStationSpaceByCityId(long cityId);
	/**
	 * 通过cityId获取站间距的lte小区参数
	 */
	public List<Map<String, Object>> queryCellParmsByCityId(long cityId);
}
