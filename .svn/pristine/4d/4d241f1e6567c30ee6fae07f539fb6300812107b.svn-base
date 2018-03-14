package com.hgicreate.rno.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hgicreate.rno.mapper.G4AzimuthCalcMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("sysAreaSyncServiceImpl")
public class SysAreaSyncServiceImpl implements SysAreaSyncService{
	
	@Autowired
	private G4AzimuthCalcMapper g4AzimuthCalcMapper;

	public  Map<Long, List<Long>> cityToSubAreaS = new HashMap<Long, List<Long>>();
	
	volatile boolean hasInit = false;
	
	private void initData() {
		synchronized (SysAreaSyncServiceImpl.class) {
			if (!hasInit) {
				hasInit = true;
				
				List<Map<String, Object>> areaMaps = g4AzimuthCalcMapper.getAllAreaList();
				
				if (areaMaps != null) {
					long id = -1, parentId = -1;
					java.util.List<Long> subIds = null;
					for (Map<String, Object> areaMap : areaMaps) {
						if (areaMap.get("AREA_ID") != null) {
							try {
								id = Long.parseLong(areaMap.get("AREA_ID")
										.toString());
								parentId = Long.parseLong(areaMap.get(
										"PARENT_ID").toString());
								subIds = cityToSubAreaS.get(parentId);
								if (subIds == null) {
									subIds = new ArrayList<Long>();
									cityToSubAreaS.put(parentId, (List<Long>) subIds);
								}
								subIds.add(id);
							} catch (Exception e) {

							}

						}
					}
				}
			}
		}
	}
	/**
	 * 
	* @Title: 获取包括自身以及子区域id 在内的逗号分隔的字符串
	* @Description: 
	* @Company:  怡创科技
	* @param parId
	* @return
	* @return: String
	* @author chao_xj
	* @date 2016年6月4日
	 */
	public String getSubAreaAndSelfIdListStrByParentId(long parId){
		if (!hasInit){
			initData();
		}
		List<Long> subIds=cityToSubAreaS.get(parId);
		if(subIds!=null && subIds.size()>0){
			String tmp=parId+",";
			for(long id:subIds){
				tmp+=id+",";
			}
			if(tmp.length()>0){
				tmp=tmp.substring(0, tmp.length()-1);
			}
			return tmp;
		}
		return null;
		
	}
}
