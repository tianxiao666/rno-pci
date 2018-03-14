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

import com.hgicreate.rno.mapper.oracle.OracleMapper;

@Repository
public class ServiceForPciEvalDaoImpl implements ServiceForPciEvalDao {

	private static Logger logger = LoggerFactory.getLogger(ServiceForPciEvalDaoImpl.class);
	
	@Autowired
	private OracleMapper oracleMapper;
	
	/**
	 * 获取用户可访问的指定级别的区域
	 * 
	 * @param account
	 * @return
	 */
	@Override
	public List<Map<String, Object>> getSpecialLevalAreaByAccount(String account) {
		logger.debug("account={}", account);
		return oracleMapper.getSpecialLevalAreaByAccount(account);
	}

	/**
	 * 获取指定区域集合的所有上级区域
	 * 
	 * @param area_ids
	 * @param areaLevel
	 * @return
	 */
	@Override
	public List<Map<String, Object>> getSpecialUpperAreas(long[] area_ids, String areaLevel) {

		if (area_ids.length == 0) {
			return Collections.emptyList();
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("area_ids", area_ids);
		map.put("areaLevel", areaLevel);
		List<Map<String, Object>> areaList = oracleMapper.getSpecialUpperAreas(map);
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		if (areaList != null) {
			for (Map<String, Object> one : areaList) {
				if (one.get("NAME") != null) {
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
		result = oracleMapper.getUserConfigAreaId(account);
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
		result = oracleMapper.getParentIdByCityId(cfgCityId);
		logger.debug("getParentIdByCityId的结果为，cfgCityId={}", result);
		return result;
	}
	
	/**
	 * 获取同站lte小区和pci
	 */
	public List<Map<String, String>> getSameStationCellsByLteCellId(String lteCell){
		// 获取服务小区信息
		List<Map<String, Object>> serverCellDetail = oracleMapper.getServerCellDetailByCellId(lteCell);
		
		if (serverCellDetail.size() <= 0) {
			return Collections.emptyList();
		}
		String cellName = serverCellDetail.get(0).get("CELL_ID").toString();
		String cellPci = serverCellDetail.get(0).get("PCI").toString();
		String cellENodeBId = serverCellDetail.get(0).get("ENODEB_ID").toString();
		Map<String, String> cond = new HashMap<String, String>();
		cond.put("cellENodeBId", cellENodeBId);
		cond.put("lteCell", lteCell);
		// 获取同站小区信息
		List<Map<String, Object>> cellsDetail = oracleMapper.getSameStationCellDetailByCellId(cond);
		// 整理结果
		List<Map<String, String>> result = new ArrayList<Map<String, String>>();
		Map<String, String> one = new HashMap<String, String>();
		one.put("CELL", cellName);
		one.put("PCI", cellPci);
		result.add(one);
		for (Map<String, Object> map : cellsDetail) {
			one = new HashMap<String, String>();
			cellName = map.get("CELL_ID").toString();
			cellPci = map.get("PCI").toString();
			one.put("CELL", cellName);
			one.put("PCI", cellPci);
			result.add(one);
		}

		return result;
	}
	
	/**
	 * 转换lte小区与某同站小区的pci
	 */
	public boolean changeLteCellPci(final String cell1, final String pci1, final String cell2, final String pci2){
		
		int resCnt1 = oracleMapper.changeLteCellPci(pci1, cell1);
		int resCnt2 = oracleMapper.changeLteCellPci(pci2, cell2);
		
		if (resCnt1 > 0 && resCnt2 > 0) {
			return true;
		} else {
			return false;
		}
	}
}
