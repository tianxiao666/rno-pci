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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.hgicreate.rno.dao.RnoCommonDao;
import com.hgicreate.rno.mapper.LteDynaOverGraphMapper;
import com.hgicreate.rno.model.Area;

@Service("rnoCommonServiceImpl")
public class RnoCommonServiceImpl implements RnoCommonService {

	private static Logger logger = LoggerFactory.getLogger(RnoCommonServiceImpl.class);

	@Autowired
	@Qualifier("rnoCommonDaoImpl")
	private RnoCommonDao rnoCommonDao;

	private List<Area> allAreas;
	@Autowired
	private LteDynaOverGraphMapper lteDynaOverGraphMapper;
	/**
	 * 获取用户可访问的指定级别的区域
	 * 
	 * @param account
	 * @return
	 */
	@Override
	public Map<String, List<Area>> getAreaByAccount(String account) {
		Map<String, List<Area>> map = new HashMap<String, List<Area>>();
		List<Area> provinceAreas = getSpecialArea(account, "省");
		List<Area> cityAreas = new ArrayList<Area>();
		List<Area> countryAreas = new ArrayList<Area>();
		List<Area> centerPoint = new ArrayList<Area>();// 中心点
		// 通过获取默认城市，然后置顶
		long cfgCityId = rnoCommonDao.getUserConfigAreaId(account);
		long cfgProvinceId = 0;
		if (cfgCityId != -1) {
			cfgProvinceId = rnoCommonDao.getParentIdByCityId(cfgCityId);
		}

		// 再恢复数据源
		provinceAreas = getSpecialArea(account, "省");

		// 如果该帐户没有设定过默认区域，哪就默认第一个省份为默认区域
		if (cfgProvinceId == 0) {
			cfgProvinceId = provinceAreas.get(0).getAreaId();
		}

		// 先保存第一个省的对象信息:为了使默认的省排在首位
		Area tmp = provinceAreas.get(0);
		for (int i = 0; i < provinceAreas.size(); i++) {
			// 替换次序
			if (provinceAreas.get(i).getAreaId() == cfgProvinceId) {
				provinceAreas.set(0, provinceAreas.get(i));
				provinceAreas.set(i, tmp);
				break;
			}
		}

		// 获取用默认地市所在省的所有地市，并把默认地市与排在第一位的地市交换位置
		if (provinceAreas != null && provinceAreas.size() > 0) {
			cityAreas = getSpecialSubAreasByAccountAndParentArea(account, cfgProvinceId, "市");
			Area areaTemp = new Area();
			for (int j = 0; j < cityAreas.size(); j++) {
				if (cityAreas.get(j).getAreaId() == cfgCityId) {
					areaTemp = cityAreas.get(0);
					cityAreas.set(0, cityAreas.get(j));
					cityAreas.set(j, areaTemp);
				}
			}
		}
		if (cityAreas != null && cityAreas.size() > 0) {
			countryAreas = getSpecialSubAreasByAccountAndParentArea(account,
							cityAreas.get(0).getAreaId(), "区/县");
			if (countryAreas != null && countryAreas.size() > 0) {
//				areaName = countryAreas.get(0).getName();
				Area area = new Area();
				logger.debug("lat={},lon={}",countryAreas.get(0).getLat(),countryAreas.get(0).getLon());
				area.setLon(countryAreas.get(0).getLon());
				area.setLat(countryAreas.get(0).getLat());
				centerPoint.add(area);
			}
		}
		logger.debug("cityAreas={}", cityAreas);
		map.put("centerPoint", centerPoint);
		map.put("provinceAreas", provinceAreas);
		map.put("cityAreas", cityAreas);
		map.put("countryAreas", countryAreas);
		map.put("allAreas", allAreas);

		return map;
	}


	/**
	 * 获取用户可访问的指定级别的区域
	 * 
	 * @param account
	 * @param areaLevel
	 * @return
	 */
	public List<Area> getSpecialArea(String account, String areaLevel) {
		logger.debug("进入getSpecialArea：account={}, areaLevel={}", account, areaLevel);

		List<Area> user_area_list = rnoCommonDao.getSpecialLevalAreaByAccount(account);
		if (user_area_list == null || user_area_list.size() == 0) {
			return null;
		}
		HashSet<Long> areaIdSet = new HashSet<Long>();

		for (Area usea : user_area_list) {
			areaIdSet.add(usea.getAreaId());
		}

		long[] areaIds = new long[areaIdSet.size()];
		int count = 0;
		Iterator<Long> iter = areaIdSet.iterator();
		while (iter.hasNext()) {
			areaIds[count++] = iter.next();
		}

		// 所有指定级别的列表
		List<Area> result = rnoCommonDao.getSpecialUpperAreas(areaIds, areaLevel);
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
	public List<Area> getSpecialSubAreasByAccountAndParentArea(String account, long parentAreaId, String subAreaLevel) {
		allAreas = getSpecialArea(account, subAreaLevel);
		// 判断是否属于指定parentAreaId下的
		List<Area> subAreas = new ArrayList<Area>();
		if (allAreas != null && allAreas.size() > 0) {
			for (Area area : allAreas) {
				if (area.getParentId() == parentAreaId) {
					subAreas.add(area);
				}
			}
		}
		return subAreas;
	}

}
