package com.hgicreate.rno.service;

import java.util.List;
import java.util.Map;

import com.hgicreate.rno.model.Area;

public interface RnoLteDynaOverGraphService {

	/**
	 * 
	* @Title: 获取用户可访问的指定级别的区域
	* @Description: 
	* @Company:  怡创科技
	* @param accountId
	* @param areaLevel
	* @return
	* @return: List<Area>
	* @author chao_xj
	* @date 2016年6月3日
	 */
	public List<Area> gisfindAreaInSpecLevelListByUserId(String accountId,
                                                         String areaLevel);

	/*
	 * 获取画LTE小区动态覆盖图(贝塞尔曲线)所需的数据
	 */
	/*public Map<String, List<Map<String, Object>>> get4GDynaCoverageDataByCityAndDate(
            String lteCellId, long cityId, String startDate, String endDate, double imgSizeCoeff);*/
	/*
	 * 获取画LTE小区动态覆盖图(折线)所需的数据
	 */
	public Map<String, List<Map<String, Object>>> get4GDynaCoverageData2ByCityAndDate(
            String lteCellId, long cityId, String startDate, String endDate,
            double imgCoeff, double imgSizeCoeff);
	/*
	 * 获取画LTE小区动态覆盖 in干扰所需的数据【小区位置】
	 */
	public List<Map<String,String>> get4GDynaCoverageInInferDataByCityAndDate(
            String lteCellId, long cityId, String startDate, String endDate);
	/*
	 * 获取画LTE小区动态覆盖 out干扰所需的数据[邻区位置]
	 */
	public List<Map<String,String>> get4GDynaCoverageOutInferDataByCityAndDate(
            String lteCellId, long cityId, String startDate, String endDate);
}
