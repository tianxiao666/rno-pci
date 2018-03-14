package com.hgicreate.rno.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.hgicreate.rno.mapper.G4AzimuthCalcMapper;
import com.hgicreate.rno.model.Area;
import com.hgicreate.rno.model.Report;

@Repository("rnoCommonDaoImpl")
public class RnoCommonDaoImpl implements RnoCommonDao {

	private static Logger logger = LoggerFactory.getLogger(RnoCommonDaoImpl.class);

	@Autowired
	private G4AzimuthCalcMapper g4AzimuthCalcMapper;

	/**
	 * 获取用户可访问的指定级别的区域
	 * 
	 * @param account
	 * @return
	 */
	@Override
	public List<Area> getSpecialLevalAreaByAccount(String account) {
		logger.debug("account={}", account);
		return g4AzimuthCalcMapper.getSpecialLevalAreaByAccount(account);
	}

	/**
	 * 获取指定区域集合的所有上级区域
	 * 
	 * @param area_ids
	 * @param areaLevel
	 * @return
	 */
	@Override
	public List<Area> getSpecialUpperAreas(long[] area_ids, String areaLevel) {

		if (area_ids.length == 0) {
			return Collections.emptyList();
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("area_ids", area_ids);
		map.put("areaLevel", areaLevel);
		List<Area> areaList = g4AzimuthCalcMapper.getSpecialUpperAreas(map);
		List<Area> result = new ArrayList<Area>();
		if (areaList != null) {
			for (Area one : areaList) {
				if (one.getName() != null) {
					result.add(one);
				}
			}
		}
		return result;
	}

	/**
	 * 获取用户配置的默认城市id
	 * 
	 * @param account
	 * @return
	 */
	@Override
	public long getUserConfigAreaId(String account) {

		long result = -1;
		result = g4AzimuthCalcMapper.getUserConfigAreaId(account);
		logger.debug("getUserConfigAreaId的结果为，cityId={}", result);
		return result;
	}

	/**
	 * 通过城市ID获取父ID即省
	 * 
	 * @param cfgCityId
	 * @return
	 */
	@Override
	public long getParentIdByCityId(long cfgCityId) {

		long result = -1;
		result = g4AzimuthCalcMapper.getParentIdByCityId(cfgCityId);
		logger.debug("getParentIdByCityId的结果为，cfgCityId={}", result);
		return result;
	}


	/**
	 * 查询某job的报告数量
	 * 
	 * @param jobId
	 * @return
	 */
	@Override
	public int queryJobReportCnt(Long jobId) {
		int result = -1;
		result = g4AzimuthCalcMapper.queryJobReportCnt(jobId);
		logger.debug("queryUploadDataCnt的结果为，result={}", result);
		return result;
	}

	/**
	 * 分页查询某job的业务报告
	 * 
	 * @param map
	 * @return
	 */
	@Override
	public List<Report> queryJobReportByPage(Map<String, Object> map) {
		return g4AzimuthCalcMapper.queryJobReportByPage(map);
	}

}
