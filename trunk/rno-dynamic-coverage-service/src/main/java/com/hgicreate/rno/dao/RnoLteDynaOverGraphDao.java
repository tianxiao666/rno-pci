package com.hgicreate.rno.dao;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

public interface RnoLteDynaOverGraphDao {

	/**
	 * 获取某个地市 的小区频段类型信息
	 */
	public Map<String, Object> queryLteCellShapeDataByCityId(long cityId);
	/**
	 * 从spark数据查询动态覆盖数据
	 * @param cityId
	 * @param lteCellId
	 * @param startDate
	 * @param endDate
	 * @param inOrOut
	 * out 出干扰：即被别人所检测  in 入干扰：即检测到别人
	 * @return
	 */
	public Map<String,List<Map<String,Object>>> queryDynaCoverDataFromSparkMrTable(
			long cityId, String lteCellId,
			String startDate, String endDate,String inOrOut,Connection connection);
}
