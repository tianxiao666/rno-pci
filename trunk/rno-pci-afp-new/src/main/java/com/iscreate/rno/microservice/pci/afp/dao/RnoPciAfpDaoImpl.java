package com.iscreate.rno.microservice.pci.afp.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.iscreate.rno.microservice.pci.afp.mapper.oracle.OracleMapper;
import com.iscreate.rno.microservice.pci.afp.mapper.spark.SparkMapper;
import com.iscreate.rno.microservice.pci.afp.model.G4SfDescQueryCond;
import com.iscreate.rno.microservice.pci.afp.model.RnoLteInterferCalcTask.TaskInfo;
import com.iscreate.rno.microservice.pci.afp.model.RnoThreshold;
import com.iscreate.rno.microservice.pci.afp.model.TaskCond;

@Repository("rnoPciAfpDaoImpl")
public class RnoPciAfpDaoImpl implements RnoPciAfpDao {

	private static final Logger logger = LoggerFactory.getLogger(RnoPciAfpDaoImpl.class);

	@Autowired
	private OracleMapper oracleMapper;
	
	@Autowired
	private SparkMapper sparkMapper;

	/**
	 * 
	 * @title 通过区域及起始时间和厂家,及数据类型从Hbase获取MR数据描述记录情况
	 * @param cityId
	 * @param startTime
	 * @param endTime
	 * @param factory
	 *            ERI,ZTE
	 * @param dataType
	 *            HO,MR
	 * @return
	 */
	@Override
	public List<Map<String, Object>> getDataDescRecordFromHbase(long cityId, String startTime, String endTime,
			String factory, String dataType) {
				return null;
		
	}

	/**
	 * 分页查询扫频数据
	 * 
	 * @param cond
	 * @return
	 */
	@Override
	public List<Map<String, String>> querySfDataFromHbaseByPage(G4SfDescQueryCond cond) {
		return sparkMapper.querySFfile(cond);
		
	}

	/**
	 * 统计pci自动规划任务数量
	 * 
	 */
	public long getPciAnalysisTaskCount(TaskCond cond) {
		logger.debug("进入方法：getPciAnalysisTaskCount。");
		long result = oracleMapper.getPciAnalysisTaskCount(cond);
		return result;
	}

	/**
	 * 分页获取pci自动规划任务信息
	 * 
	 * @author peng.jm
	 * @date 2015年3月31日15:23:10
	 */
	public List<Map<String, Object>> queryPciPlanTaskByPage(TaskCond cond) {
		logger.debug("进入方法：queryPciPlanTaskByPage。");
		List<Map<String, Object>> res = oracleMapper.queryPciPlanTaskByPage(cond);
		return res;
	}

	/**
	 * 
	 */
	public Map<String, Object> savePciPlanAnalysisTaskInfo(long tmpJobId, final List<RnoThreshold> rnoThresholds,
			final TaskInfo taskInfo) {
		logger.debug("进入方法：savePciPlanAnalysisTaskInfo。tmpJobId={},rnoThresholds={},taskInfo={}", tmpJobId,
				rnoThresholds, taskInfo);
		Map<String, Object> result = new HashMap<String, Object>();
		if (tmpJobId <= 0) {
			logger.error("创建jobId失败！");
			result.put("flag", false);
			result.put("desc", "提交任务失败！");
			return result;
		}

		final long jobId = tmpJobId;

		// 保存对应的门限值
		for (RnoThreshold rnoThreshold : rnoThresholds) {
			if (!StringUtils.isBlank(rnoThreshold.getCode()) && !StringUtils.isBlank(rnoThreshold.getDefaultVal())) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("jobId", jobId);
				map.put("paramType", "PCI_THRESHOLD");
				map.put("paramCode", rnoThreshold.getCode());
				map.put("paramVal", rnoThreshold.getDefaultVal());
				int result2 = oracleMapper.saveJobParam(map);
				if (result2 <= 0) {
					logger.error("jobId={}，保存PCI规划参数失败！", jobId);
					result.put("flag", false);
					result.put("desc", "提交任务失败！");
					return result;
				}
			}
		}

		// 保存PCI任务
		String dlFileName = "";
		if (!(taskInfo.getIsExportAssoTable().equals("NO") && taskInfo.getIsExportMidPlan().equals("NO")
				&& taskInfo.getIsExportNcCheckPlan().equals("NO"))) {
			dlFileName = jobId + "_PCI优化.zip";
		} else {
			dlFileName = jobId + "_PCI优化方案.xlsx";
		}
		// 读取文件名
		String rdFileName = jobId + "_pci_data";
		// 创建日期
		Calendar cal = Calendar.getInstance();
		cal = Calendar.getInstance();
		String createTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cal.getTime());
		// 文件保存路径
		String resultDir =  "/rno_data/pci/" + cal.get(Calendar.YEAR) + "/"
				+ (cal.get(Calendar.MONTH) + 1) + "/" + jobId;
		String dataFilePath = resultDir + "/" + jobId + "_pci_cal_data";
		String finishState = "排队中";
		// 更新日期
		String modTime = createTime;
		String begMeaTime = taskInfo.getStartTime();
		String endMeaTime = taskInfo.getEndTime();
		long cityId = taskInfo.getCityId();
		String optimizeCells = taskInfo.getLteCells();
		String planType = taskInfo.getPlanType();
		String converType = taskInfo.getConverType();
		String relaNumType = taskInfo.getRelaNumerType();
		String isCheckNCell = taskInfo.getIsCheckNCell();
		String isExportAssoTable = taskInfo.getIsExportAssoTable();
		String isExportMidPlan = taskInfo.getIsExportMidPlan();
		String isExportNcCheckPlan = taskInfo.getIsExportNcCheckPlan();
		String sfFiles = taskInfo.getSfFiles();
		String freqAdjType = taskInfo.getFreqAdjType();
		String d1Freq = taskInfo.getD1Freq();
		String d2Freq = taskInfo.getD2Freq();
		long matrixDcId = taskInfo.getMatrixDataCollectId();
		long flowDcId = taskInfo.getFlowDataCollectId();
		double ks = taskInfo.getKs();
		int getMatrixType = taskInfo.getGetMatrixType();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("jobId", jobId);
		map.put("dlFileName", dlFileName);
		map.put("rdFileName", rdFileName);
		map.put("createTime", createTime);
		map.put("resultDir", resultDir);
		map.put("dataFilePath", dataFilePath);
		map.put("finishState", finishState);
		map.put("modTime", modTime);
		map.put("begMeaTime", begMeaTime);
		map.put("endMeaTime", endMeaTime);
		map.put("cityId", cityId);
		map.put("optimizeCells", optimizeCells);
		map.put("planType", planType);
		map.put("converType", converType);
		map.put("relaNumType", relaNumType);
		map.put("isCheckNCell", isCheckNCell);
		map.put("isExportAssoTable", isExportAssoTable);
		map.put("isExportMidPlan", isExportMidPlan);
		map.put("isExportNcCheckPlan", isExportNcCheckPlan);
		map.put("sfFiles", sfFiles);
		map.put("freqAdjType", freqAdjType);
		map.put("d1Freq", d1Freq);
		map.put("d2Freq", d2Freq);
		map.put("matrixDcId", matrixDcId);
		map.put("flowDcId", flowDcId);
		map.put("ks", ks);
		map.put("getMatrixType", getMatrixType);
		logger.debug("map:{}", map);
		int result3 = oracleMapper.savePciTaskInfo(map);
		if (result3 <= 0) {
			logger.error("jobId={}，保存PCI规划任务失败！", jobId);
			result.put("flag", false);
			result.put("desc", "提交任务失败！");
			return result;
		} else {
			logger.debug("jobId={}，保存PCI规划任务成功！", jobId);
			result.put("flag", true);
			result.put("desc", "提交任务成功！");
			return result;
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
		List<Map<String, Object>> lteCells = sparkMapper.getParameterForCellsMap(cityId);
		if (lteCells == null || lteCells.isEmpty()) {
			return null;
		}
		String cid = "", eid = "";
		List<String> cells;
		for (Map<String, Object> map : lteCells) {
			try {
				cid = map.get("cid").toString();
				if (!cellList.contains(cid)) {
					eid = map.get("eid").toString();
					cellList.add(cid);
					cellToOriPci.put(cid, Integer.parseInt(map.get("pci").toString()));
					cellToLonLat.put(cid, new double[] { Double.parseDouble(map.get("lng").toString()),
							Double.parseDouble(map.get("lat").toString()) });
					cellToEarfcn.put(cid, Integer.parseInt(map.get("earfcn").toString()));
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

	/**
	 * 
	 * @title 通过区域ID获取LTE小区标识对应的小区信息
	 * @param cityId
	 * @return
	 */
	public Map<String, List<String>> getLteCellInfoByCellId(long cityId) {
		logger.debug("进入方法体getLteCellInfoByCellId。cityId=" + cityId);
		Map<String, List<String>> cellToCells = new HashMap<String, List<String>>();

		List<Map<String, Object>> lteCells = sparkMapper.getLteCellInfoByCellId(cityId);
		if (lteCells == null || lteCells.size() == 0) {
			return null;
		}
		String cellId = "";
		String pci = "";
		String cellName = "";
		String earfcn = "";
		for (Map<String, Object> map : lteCells) {
			// enodebId=Integer.parseInt(map.get("ENODEB_ID").toString());
			cellId = map.get("cell").toString();
			pci = map.get("pci").toString();
			cellName = map.get("name").toString();
			earfcn = map.get("earfcn").toString();
			cellToCells.put(cellId, Arrays.asList(cellName, pci, earfcn));
		}
		return cellToCells;
	}

}
