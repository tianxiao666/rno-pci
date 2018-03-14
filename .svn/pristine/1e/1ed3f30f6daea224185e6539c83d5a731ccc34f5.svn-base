package com.iscreate.rnointerfermatrix.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.iscreate.rnointerfermatrix.mapper.oracle.OracleMapper;
import com.iscreate.rnointerfermatrix.mapper.spark.SparkMapper;
import com.iscreate.rnointerfermatrix.model.G4HoDescQueryCond;
import com.iscreate.rnointerfermatrix.model.G4MrDescQueryCond;
import com.iscreate.rnointerfermatrix.model.G4SfDescQueryCond;
import com.iscreate.rnointerfermatrix.model.Page;
import com.iscreate.rnointerfermatrix.model.RnoLteInterMatrixTaskInfo;

@Repository("rnoInterferMatrixDaoImpl")
public class RnoInterferMatrixDaoImpl implements RnoInterferMatrixDao {

	private static final Logger logger = LoggerFactory.getLogger(RnoInterferMatrixDaoImpl.class);

	@Autowired
	private OracleMapper oracleMapper;
	
	@Autowired
	private SparkMapper sparkMapper;

	/**
	 * 
	 * @title 获取符合条件的4g干扰矩阵数量
	 * @param condition
	 * @return
	 */
	public long getLteInterferMartixCount(Map<String, Object> condition) {
		return oracleMapper.getLteInterferMartixCount(condition);
	}

	/**
	 * 
	 * @title 分页获取符合条件的4g干扰矩阵的详情
	 * @param condition
	 * @param startIndex
	 * @param cnt
	 * @return
	 */
	public List<Map<String, Object>> queryLteInterferMartixByPage(Map<String, Object> map) {
		logger.debug("进入方法：queryLteInterferMartixByPage。condition={}", map);
		if (map == null || map.isEmpty()) {
			logger.debug("未传入查询条件");
		}
		if (Integer.parseInt(map.get("startIndex").toString()) < 0 || Integer.parseInt(map.get("cnt").toString()) < 0) {
			return Collections.emptyList();
		}

		List<Map<String, Object>> res = oracleMapper.queryLteInterferMartixByPage(map);
		return res;
	}

	/**
	 * 根据城市ID获取任务名列表
	 * 
	 * @param cityId
	 * @return
	 */
	public List<Map<String, String>> getTaskNameListByCityId(long cityId) {
		logger.debug("进入方法getTaskNameListByCityId。cityId ={}", cityId);
		return oracleMapper.getTaskNameListByCityId(cityId);
	}

	/**
	 * 
	 * @title 检查这周是否计算过4G MR干扰矩阵
	 * @param cityId
	 * @param thisMonday
	 * @param today
	 * @return
	 */
	public List<Map<String, Object>> checkLteInterMartixThisWeek(long cityId, String thisMonday, String today) {
		logger.debug("进入方法check4GInterMartixThisWeek。areaId={}, thisMonday={}, today={}", cityId, thisMonday, today);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("cityId", cityId);
		map.put("thisMonday", thisMonday);
		map.put("today", today);

		return oracleMapper.checkLteInterMartixThisWeek(map);
	}

	/**
	 * 
	 * @title 创建MR 4g 干扰矩阵计算任务
	 * @param interMartix
	 * @return
	 */
	public boolean createLteInterMartixRec(long jobId, RnoLteInterMatrixTaskInfo taskInfo) {
		logger.debug("进入方法createMr4GInterMartixRec。taskInfo={}", taskInfo);
		// 创建日期
		Calendar cal = Calendar.getInstance();
		String createDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cal.getTime());
		// 结果文件路径
		String filePath = cal.get(Calendar.YEAR) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + jobId;
		String fileName = UUID.randomUUID().toString().replaceAll("-", "");

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("cityId", taskInfo.getCityId());
		map.put("createDate", createDate);
		map.put("startMeaDate", taskInfo.getBegTime());
		map.put("endMeaDate", taskInfo.getEndTime());
		map.put("recordNum", taskInfo.getRecordNum());
		map.put("type", taskInfo.getDataType());
		map.put("filePath", filePath);
		map.put("jobId", jobId);
		map.put("fileName", fileName);
		map.put("taskName", taskInfo.getTaskName());
		map.put("dataDesc", taskInfo.getDataDescription());
		map.put("sameFreqCellCoefWeight", taskInfo.getSameFreqCellCoefWeight());
		map.put("switchRatioWeight", taskInfo.getSwitchRatioWeight());
		map.put("sfFiles", taskInfo.getSfFiles());

		int res = oracleMapper.createLteInterMartixRec(map);
		if (res > 0) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * 通过城市ID获取小区和经纬度的映射集合
	 * 
	 * @param cityId
	 * @return
	 */
	public Map<String, Object> getParameterForCellsMap(long cityId) {
		Map<String, Object> cellToParameter = new HashMap<String, Object>();
		List<String> cellList = new ArrayList<String>();
		Map<String, Integer> cellToOriPci = new HashMap<String, Integer>();
		Map<String, double[]> cellToLonLat = new HashMap<String, double[]>();
		Map<String, Integer> cellToEarfcn = new HashMap<String, Integer>();
		Map<String, List<String>> enodebToCells = new HashMap<String, List<String>>();
		Map<String, String> cell2Enodeb = new HashMap<String, String>();
		List<Map<String, Object>> lteCells = oracleMapper.getParameterForCellsMap(cityId);
		if (lteCells == null || lteCells.isEmpty()) {
			return null;
		}
		String cid = "", eid = "";
		List<String> cells;
		for (Map<String, Object> map : lteCells) {
			try {
				cid = map.get("CID").toString();
				if (!cellList.contains(cid)) {
					eid = map.get("EID").toString();
					cellList.add(cid);
					cellToOriPci.put(cid, Integer.parseInt(map.get("PCI").toString()));
					cellToLonLat.put(cid, new double[] { Double.parseDouble(map.get("LNG").toString()),
							Double.parseDouble(map.get("LAT").toString()) });
					cellToEarfcn.put(cid, Integer.parseInt(map.get("EARFCN").toString()));
					cell2Enodeb.put(cid, eid);
					cells = enodebToCells.get(eid);
					if (cells == null) {
						cells = new ArrayList<String>();
						enodebToCells.put(eid, cells);
					}
					if (!cells.contains(cid)) {
						cells.add(cid);
					}
				}
			} catch (Exception e) {
				// e.printStackTrace();
			}
		}
		cellToParameter.put("cellList", cellList);
		cellToParameter.put("cellToOriPci", cellToOriPci);
		cellToParameter.put("cellToLonLat", cellToLonLat);
		cellToParameter.put("cellToEarfcn", cellToEarfcn);
		cellToParameter.put("enodebToCells", enodebToCells);
		cellToParameter.put("cell2Enodeb", cell2Enodeb);
		return cellToParameter;
	}

	/**
	 * 通过主进程 jobId 更新 lte_pci_job 表中 MR_JOB_ID
	 * 
	 * @param jobId
	 * @param mrJobId
	 * @param type
	 */
	public void addMapReduceJobId(long jobId, String mrJobId, String type) {
		String table = "rno_ms_lte_pci_job";
		if ("MARTIX".equals(type.toUpperCase())) {
			table = "Rno_Ms_4g_Inter_Martix_Rec";
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("jobId", jobId);
		map.put("mrJobId", mrJobId);
		map.put("table", table);
		oracleMapper.addMapReduceJobId(map);
	}

	@Override
	public int queryMrDataRecordsCount(int cityId, String begTime, String endTime) {
		Map<String, Object> map = new HashMap<>();
		map.put("beginTime", begTime.replace("-", ""));
		map.put("endTime", endTime.replace("-", ""));
		map.put("areaId", cityId);
		return sparkMapper.getMrcount(map);
	}

	@Override
	public int queryHoDataRecordsCount(int cityId, String begTime, String endTime) {
		Map<String, Object> map = new HashMap<>();
		map.put("beginTime", begTime.replace("-", ""));
		map.put("endTime", endTime.replace("-", ""));
		map.put("areaId", cityId);
		return sparkMapper.getHocount(map);
	}

	@Override
	public List<Map<String, Object>> queryMrDataFromHbaseByPage(G4MrDescQueryCond cond, Page newPage) {
		Map<String, Object> map = new HashMap<>();
		map.put("startMeaDay", cond.getMeaBegTime().replace("-", ""));
		map.put("endMeaDay", cond.getMeaEndTime().replace("-", ""));
		map.put("areaId", cond.getCityId());
		List<String> list = sparkMapper.queryMrMeaDate(map);
		if(list.isEmpty()){
			return null;
		}
		newPage.setTotalCnt((int) list.size());
		Map<String, Object> map1 = new HashMap<>();
		map1.put("meaTime", list);
		map1.put("areaId", cond.getCityId());
		return sparkMapper.queryMrCount(map1);
	}
	
	@Override
	public List<Map<String, Object>> queryHoDataFromHbaseByPage(G4HoDescQueryCond cond, Page newPage) {
		Map<String, Object> map = new HashMap<>();
		map.put("startMeaDay", cond.getMeaBegTime().replace("-", ""));
		map.put("endMeaDay", cond.getMeaEndTime().replace("-", ""));
		map.put("areaId", cond.getCityId());
		List<String> list = sparkMapper.queryHoMeaDate(map);
		if(list.isEmpty()){
			return null;
		}
		newPage.setTotalCnt((int) list.size());
		Map<String, Object> map1 = new HashMap<>();
		map1.put("meaTime", list);
		map1.put("areaId", cond.getCityId());
		return sparkMapper.queryHoCount(map1);
	}
	
	@Override
	public List<Map<String, Object>> querySfDataFromHbaseByPage(G4SfDescQueryCond cond, Page newPage) {
		Map<String, Object> map = new HashMap<>();
		map.put("startMeaDay", cond.getMeaBegTime().replace("-", ""));
		map.put("endMeaDay", cond.getMeaEndTime().replace("-", ""));
		map.put("areaId", cond.getCityId());
		List<String> list = sparkMapper.querySfMeaDate(map);
		if(list.isEmpty()){
			return null;
		}
		newPage.setTotalCnt((int) list.size());
		Map<String, Object> map1 = new HashMap<>();
		map1.put("meaTime", list);
		map1.put("areaId", cond.getCityId());
		return sparkMapper.querySfCount(map1);
	}
	
}
