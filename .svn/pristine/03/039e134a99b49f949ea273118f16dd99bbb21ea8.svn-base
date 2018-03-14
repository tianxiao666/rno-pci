package com.hgicreate.rno.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hgicreate.rno.dao.ServiceForPciEvalDao;
import com.hgicreate.rno.mapper.oracle.OracleMapper;
import com.hgicreate.rno.mapper.spark.SparkMapper;

@Service
public class ServiceForPciEvalServiceImpl implements ServiceForPciEvalService{
	
	private static Logger logger = LoggerFactory.getLogger(ServiceForPciEvalServiceImpl.class);
	
	@Autowired
	private ServiceForPciEvalDao serviceForPciEvalDao;

	@Autowired
	private OracleMapper oracleMapper;
	
	@Autowired
	private SparkMapper sparkMapper;
	
	/**
	 * 获取用户可访问的指定级别的区域
	 * 
	 * @param account
	 * @return
	 */
	@Override
	public Map<String, List<Map<String, Object>>> getAreaByAccount(String account) {
		Map<String, List<Map<String, Object>>> map = new HashMap<String, List<Map<String, Object>>>();
		List<Map<String, Object>> provinceAreas = getSpecialArea(account, "省");
		List<Map<String, Object>> cityAreas = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> countryAreas = new ArrayList<Map<String, Object>>();
		// 通过获取默认城市，然后置顶
		long cfgCityId = serviceForPciEvalDao.getUserConfigAreaId(account);
		long cfgProvinceId = 0;
		if (cfgCityId != -1) {
			cfgProvinceId = serviceForPciEvalDao.getParentIdByCityId(cfgCityId);
		}

		// 再恢复数据源
		provinceAreas = getSpecialArea(account, "省");

		// 如果该帐户没有设定过默认区域，哪就默认第一个省份为默认区域
		if (cfgProvinceId == 0) {
			cfgProvinceId = Long.parseLong(provinceAreas.get(0).get("AREA_ID").toString());
		}

		// 先保存第一个省的对象信息:为了使默认的省排在首位
		Map<String, Object> tmp = provinceAreas.get(0);
		for (int i = 0; i < provinceAreas.size(); i++) {
			// 替换次序
			if (Long.parseLong(provinceAreas.get(i).get("AREA_ID").toString()) == cfgProvinceId) {
				provinceAreas.set(0, provinceAreas.get(i));
				provinceAreas.set(i, tmp);
				break;
			}
		}

		// 获取用默认地市所在省的所有地市，并把默认地市与排在第一位的地市交换位置
		if (provinceAreas != null && provinceAreas.size() > 0) {
			cityAreas = getSpecialSubAreasByAccountAndParentArea(account, cfgProvinceId, "市");
			Map<String, Object> areaTemp = new HashMap<String, Object>();
			for (int j = 0; j < cityAreas.size(); j++) {
				if (Long.parseLong(cityAreas.get(j).get("AREA_ID").toString()) == cfgCityId) {
					areaTemp = cityAreas.get(0);
					cityAreas.set(0, cityAreas.get(j));
					cityAreas.set(j, areaTemp);
				}
			}
		}
		if (cityAreas != null && cityAreas.size() > 0) {
			countryAreas = getSpecialSubAreasByAccountAndParentArea(account,
							Long.parseLong(cityAreas.get(0).get("AREA_ID").toString()), "区/县");
		}
		logger.debug("cityAreas={}", cityAreas);
		map.put("provinceAreas", provinceAreas);
		map.put("cityAreas", cityAreas);
		map.put("countryAreas", countryAreas);

		return map;
	}
	
	/**
	 * 获取用户可访问的指定级别的区域
	 * 
	 * @param account
	 * @param areaLevel
	 * @return
	 */
	public List<Map<String, Object>> getSpecialArea(String account, String areaLevel) {
		logger.debug("进入getSpecialArea：account={}, areaLevel={}", account, areaLevel);

		List<Map<String, Object>> user_area_list = serviceForPciEvalDao.getSpecialLevalAreaByAccount(account);
		if (user_area_list == null || user_area_list.size() == 0) {
			return null;
		}
		HashSet<Long> areaIdSet = new HashSet<Long>();

		for (Map<String, Object> usea : user_area_list) {
			areaIdSet.add(Long.parseLong(usea.get("AREA_ID").toString()));
		}

		long[] areaIds = new long[areaIdSet.size()];
		int count = 0;
		Iterator<Long> iter = areaIdSet.iterator();
		while (iter.hasNext()) {
			areaIds[count++] = iter.next();
		}

		// 所有指定级别的列表
		List<Map<String, Object>> result = serviceForPciEvalDao.getSpecialUpperAreas(areaIds, areaLevel);
		return result;
	}

	/**
	 * 根据父区域，获取用户可访问的指定级别的子区域
	 * 
	 * @param account
	 * @param parentAreaId
	 * @param subAreaLevel
	 * @return
	 */
	public List<Map<String, Object>> getSpecialSubAreasByAccountAndParentArea(String account, long parentAreaId, String subAreaLevel) {
		List<Map<String, Object>> allAreas = getSpecialArea(account, subAreaLevel);
		// 判断是否属于指定parentAreaId下的
		List<Map<String, Object>> subAreas = new ArrayList<Map<String, Object>>();
		if (allAreas != null && allAreas.size() > 0) {
			for (Map<String, Object> area : allAreas) {
				if (Long.parseLong(area.get("PARENT_ID").toString()) == parentAreaId) {
					subAreas.add(area);
				}
			}
		}
		return subAreas;
	}
	
	/**
	 * 获取最近十次 LTE 干扰矩阵信息
	 */
	@Override
	public List<Map<String, Object>> getLatelyLteMatrixByCityId(long cityId) {
		return oracleMapper.getLatelyLteMatrixByCityId(cityId);
	}
	
	/**
	 * 获取同站lte小区和pci
	 */
	public List<Map<String, String>> getSameStationCellsByLteCellId(String lteCell) {
		return serviceForPciEvalDao.getSameStationCellsByLteCellId(lteCell);
	}
	
	/**
	 * 转换lte小区与某同站小区的pci
	 */
	public Map<String, Boolean> changeLteCellPci(String cell1, String pci1, String cell2, String pci2) {
		boolean result =  serviceForPciEvalDao.changeLteCellPci(cell1,pci1,cell2,pci2);
		Map<String, Boolean> map = new HashMap<>();
		if(result){
			map.put("flag", true);
		}else{
			map.put("flag", false);
		}
		return map;
	}

	@Override
	public List<Map<String, Object>> getInCellInfo(int jobId, String cellId) {
		Map<String, Object> map = new HashMap<>();
		map.put("jobId", jobId);
		map.put("cellId", cellId);
		return sparkMapper.getInCellInfo(map);
	}

	@Override
	public List<Map<String, Object>> getOutCellInfo(int jobId, String ncellId) {
		Map<String, Object> map = new HashMap<>();
		map.put("jobId", jobId);
		map.put("ncellId", ncellId);
		return sparkMapper.getOutCellInfo(map);
	}

	@Override
	public List<Map<String, Object>> getPciCellInfo(int jobId, String cellId) {
		Map<String, Object> map = new HashMap<>();
		map.put("jobId", jobId);
		map.put("cellId", cellId);
		return sparkMapper.getPciCellInfo(map);
	}
	
}
