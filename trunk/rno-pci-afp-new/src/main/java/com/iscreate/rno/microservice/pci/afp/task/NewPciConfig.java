package com.iscreate.rno.microservice.pci.afp.task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.iscreate.rno.microservice.pci.afp.dao.RnoPciAfpDao;
import com.iscreate.rno.microservice.pci.afp.mapper.oracle.OracleMapper;
import com.iscreate.rno.microservice.pci.afp.mapper.spark.SparkMapper;
import com.iscreate.rno.microservice.pci.afp.model.DataParseStatus;
import com.iscreate.rno.microservice.pci.afp.model.JobParseStatus;
import com.iscreate.rno.microservice.pci.afp.model.JobProfile;
import com.iscreate.rno.microservice.pci.afp.model.Report;
import com.iscreate.rno.microservice.pci.afp.model.RnoThreshold;

public strictfp class NewPciConfig {

	private static final Logger logger = LoggerFactory.getLogger(NewPciConfig.class);

	private OracleMapper oracleMapper;
	
	private SparkMapper sparkMapper;

	private RnoPciAfpDao rnoPciAfpDao;
	
	private double m3r = 1;
	private double m6r = 0.8;
	private double m30r = 0.1;
	private double topRate = 0.1; // top n%
	private double defInterRate = 0.05; // 给定的干扰差值比例m
	private double defVariance = 0.05; // 给定的方差值
	private int divideNumber = 10;
	private double dtKs = -1.0;

	private String planType = "ONE"; // 评估方案，默认1
	private String schemeType = "ONE"; // 收敛方案，默认1
	private boolean isCheckNCell; // 邻区核查，默认进行
	private boolean isExportAssoTable;// 关联表输出，默认不输出
	private boolean isExportMidPlan;// 中间方案输出，默认不输出
	private boolean isExportNcCheckPlan;// 邻区核查方案输出，默认不输出

	private boolean isUseSf = false;

	private String d1D2Type = "";

	private String d1Freq = "";

	private String d2Freq = "";

	private long jobId = -1;
	private long matrixDcId ;
	private String filePath = "";
	private String fileName = "";
	private String redOutPath = ""; // reduce缺省输出目录
	// 保存返回信息
	private String returnInfo = "";

	/** 距离限制，单位米 **/
	private double dislimit = 5000.0;

	/** 变小区表 **/
	private List<String> cellsNeedtoAssignList;
	
	@Value("rsrp0minus1weight")
	private double rsrp0minus1weight;
    
	@Value("rsrp1weight")
	private double rsrp1weight;

	/**
	 * 小区与同站其他小区列的映射，同站其他小区已按关联度从大到小排列 <br>
	 * 比如key为1 <br>
	 * 干扰矩阵为 <br>
	 * 1->2 <br>
	 * 1->3 <br>
	 * 同站
	 **/
	private Map<String, List<String>> cellToSameStationOtherCells = new HashMap<String, List<String>>();

	/**
	 * 小区与非同站小区列表的映射，非同站小区已按关联度从大到小排列 <br>
	 * 比如key为1 、 <br>
	 * 干扰矩阵为 <br>
	 * 1->12 <br>
	 * 1->23 <br>
	 * 非同站
	 **/
	private Map<String, List<String>> cellToNotSameStationCells = new HashMap<String, List<String>>();

	/**
	 * 邻区与非同站小区列表的映射，非同站小区已按关联度从大到小排列 <br>
	 * 比如key为1 <br>
	 * 干扰矩阵为 <br>
	 * 12->1 <br>
	 * 23->1 <br>
	 * 非同站
	 **/
	private Map<String, List<String>> ncellToNotSameStationCells = new HashMap<String, List<String>>();

	/** 小区与邻区关联度的映射（包含了同站其他小区） 以主小区为key **/
	private Map<String, Map<String, Double>> cellToNcellAssocDegree = new HashMap<String, Map<String, Double>>();

	/** 小区与邻区关联度的映射 以邻小区为key **/
	private Map<String, Map<String, Double>> ncellToCellAssocDegree = new HashMap<String, Map<String, Double>>();

	/** 小区与小区总关联度的映射 **/
	private Map<String, Double> cellToTotalAssocDegree = new HashMap<String, Double>();

	/** 小区列表 **/
	private final List<String> cellList = new ArrayList<String>();

	/** 小区与原PCI的映射 **/
	private final Map<String, Integer> cellToOriPci = new HashMap<String, Integer>();

	/** 小区到经纬度的映射，不重复 **/
	private final Map<String, double[]> cellToLonLat = new HashMap<String, double[]>();

	/** 小区与频率的映射 **/
	private final Map<String, Integer> cellToEarfcn = new HashMap<String, Integer>();

	/** 基站到小区列表的映射 **/
	private final Map<String, List<String>> enodebToCells = new HashMap<String, List<String>>();

	/** 小区到基站的映射 **/
	private final Map<String, String> cell2Enodeb = new HashMap<String, String>();

	/** d1小区列表 **/
	private List<String> abCellList = new ArrayList<String>();

	private List<String> fileNameList = new ArrayList<String>();
	/** 文件名与ID的映射 **/
	private Map<String, String> fn2Id = new HashMap<String, String>();

	// 干扰矩阵上传文件路径
	private String matrixDfPath;

	// 小区与KS值的映射
	private Map<String, Double> cellToKs = new HashMap<String, Double>();

	public NewPciConfig(long jobId, OracleMapper oracleMapper, SparkMapper sparkMapper, RnoPciAfpDao rnoPciAfpDao) {
		this.jobId = jobId;
		this.oracleMapper = oracleMapper;
		this.sparkMapper = sparkMapper;
		this.rnoPciAfpDao = rnoPciAfpDao;
	}

	public double getM3r() {
		return m3r;
	}

	public double getM6r() {
		return m6r;
	}

	public double getM30r() {
		return m30r;
	}

	public double getTopRate() {
		return topRate;
	}

	public double getDefInterRate() {
		return defInterRate;
	}

	public double getDefVariance() {
		return defVariance;
	}

	public String getPlanType() {
		return planType;
	}

	public String getSchemeType() {
		return schemeType;
	}

	public Long getJobId() {
		return jobId;
	}

	public String getFilePath() {
		return filePath;
	}

	public String getFileName() {
		return fileName;
	}

	public List<String> getCellsNeedtoAssignList() {
		return cellsNeedtoAssignList;
	}

	public Map<String, List<String>> getEnodebToCells() {
		return enodebToCells;
	}

	public int getDivideNumber() {
		return divideNumber;
	}

	public Double getDislimit() {
		return dislimit;
	}

	public Double getDtKs() {
		return dtKs;
	}

	public Map<String, List<String>> getCellToSameStationOtherCells() {
		return cellToSameStationOtherCells;
	}

	public Map<String, List<String>> getCellToNotSameStationCells() {
		return cellToNotSameStationCells;
	}

	public Map<String, List<String>> getNcellToNotSameStationCells() {
		return ncellToNotSameStationCells;
	}

	public Map<String, Map<String, Double>> getCellToNcellAssocDegree() {
		return cellToNcellAssocDegree;
	}

	public Map<String, Map<String, Double>> getNcellToCellAssocDegree() {
		return ncellToCellAssocDegree;
	}

	public Map<String, Double> getCellToTotalAssocDegree() {
		return cellToTotalAssocDegree;
	}

	public Map<String, Integer> getCellToOriPci() {
		return cellToOriPci;
	}

	public Map<String, double[]> getCellToLonLat() {
		return cellToLonLat;
	}

	public Map<String, Integer> getCellToEarfcn() {
		return cellToEarfcn;
	}

	public String getReturnInfo() {
		return returnInfo;
	}

	public void setReturnInfo(String returnInfo) {
		this.returnInfo = returnInfo;
	}

	public String getD1D2Type() {
		return d1D2Type;
	}

	public List<String> getAbCellList() {
		return abCellList;
	}

	public String getD1Freq() {
		return d1Freq;
	}

	public String getD2Freq() {
		return d2Freq;
	}

	public boolean isCheckNCell() {
		return isCheckNCell;
	}

	public boolean isExportAssoTable() {
		return isExportAssoTable;
	}

	public boolean isExportMidPlan() {
		return isExportMidPlan;
	}

	public boolean isExportNcCheckPlan() {
		return isExportNcCheckPlan;
	}

	public String getMatrixDfPath() {
		return matrixDfPath;
	}

	public Map<String, Double> getCellToKs() {
		return cellToKs;
	}

	public String getRedOutPath() {
		return redOutPath;
	}

	public Map<String, String> getCell2Enodeb() {
		return cell2Enodeb;
	}

	@Override
	public String toString() {
		return "NewPciConfig [isCheckNCell=" + isCheckNCell + ", isExportAssoTable="
				+ isExportAssoTable + ", isExportMidPlan=" + isExportMidPlan + ", isExportNcCheckPlan="
				+ isExportNcCheckPlan + ", isUseSf=" + isUseSf + ", jobId=" + jobId + "]";
	}

	@SuppressWarnings("unchecked")
	public boolean buildPciTaskConf() {
		JobProfile job = new JobProfile(jobId);
		Date startTime = new Date();
		String msg = "";

		// 通过 jobId 获取干扰矩阵计算记录信息(rno_lte_pci_job表），包括变小区的 CLOB 信息
		List<Map<String, Object>> rec = oracleMapper.queryPciPlanJobRecByJobId(jobId);
		if (rec == null || rec.size() == 0) {
			msg = "任务配置信息不存在";
			logger.debug(msg);
			addReport(startTime, msg, DataParseStatus.Fail.toString(), "获取任务配置信息");
			updateJobEndTime(job, JobParseStatus.Fail.toString());
			return false;
		}
		Map<String, Object> pciPlanRec = rec.get(0);
		String optimizeCells = null;
		try {
			Clob clob = (Clob) pciPlanRec.get("OPTIMIZE_CELLS");
			optimizeCells = clob == null ? "" : clob.getSubString(1, (int) clob.length());
		} catch (SQLException e) {
			e.printStackTrace();
			msg = "任务配置信息不存在";
			logger.debug(msg);
			addReport(startTime, msg, DataParseStatus.Fail.toString(), "获取任务配置信息");
			updateJobEndTime(job, JobParseStatus.Fail.toString());
			return false;
		}

		String cellArr[] = null;
		if (optimizeCells != null && !"".equals(optimizeCells.trim())) {
			cellArr = optimizeCells.split(",");
			if (cellArr.length == 0) {
				// 保存报告信息
				msg = "变PCI小区字符串逗号分割后的长度为０,不满足基本需求！";
				logger.debug(msg);
				addReport(startTime, msg, DataParseStatus.Fail.toString(), "变PCI小区字符串逗号分割后的长度为０,不满足基本需求！");
				updateJobEndTime(job, JobParseStatus.Fail.toString());
				return false;
			}
		} else {
			// 保存报告信息
			msg = "变PCI小区字符串为NULL！";
			logger.debug(msg);
			addReport(startTime, msg, DataParseStatus.Fail.toString(), "变PCI小区字符串为NULL！");
			updateJobEndTime(job, JobParseStatus.Fail.toString());
			return false;
		}
		this.cellsNeedtoAssignList = Arrays.asList(cellArr);

		long cityId = Long.parseLong(pciPlanRec.get("CITY_ID").toString());

		Map<String, Object> cellToParameter = rnoPciAfpDao.getParameterForCellsMap(cityId);
		if (cellToParameter == null || cellToParameter.isEmpty()) {
			msg = "该区域下的lte小区数据不存在";
			logger.debug(msg);
			addReport(startTime, msg, DataParseStatus.Fail.toString(), "通过城市ID获取小区工参");
			updateJobEndTime(job, JobParseStatus.Fail.toString());
			return false;
		}
		cellList.addAll((List<String>) cellToParameter.get("cellList"));
		cellToLonLat.putAll((Map<String, double[]>) cellToParameter.get("cellToLonLat"));
		cellToOriPci.putAll((Map<String, Integer>) cellToParameter.get("cellToOriPci"));
		cellToEarfcn.putAll((Map<String, Integer>) cellToParameter.get("cellToEarfcn"));
		enodebToCells.putAll((Map<String, List<String>>) cellToParameter.get("enodebToCells"));
		cell2Enodeb.putAll((Map<String, String>) cellToParameter.get("cell2Enodeb"));
		cellToParameter = null;
		// 处理同站小区
		List<String> sstCells;
		for (String cellId : cellList) {
			sstCells = new ArrayList<String>();
			for (String cellTmp : enodebToCells.get(cell2Enodeb.get(cellId))) {
				if (!cellTmp.equals(cellId) && cellToEarfcn.get(cellId).equals(cellToEarfcn.get(cellTmp))) {
					sstCells.add(cellTmp.intern());
				}
			}
			cellToSameStationOtherCells.put(cellId.intern(), sstCells);
		}

		String startMeaDate = pciPlanRec.get("BEG_MEA_TIME").toString();
		String endMeaDate = pciPlanRec.get("END_MEA_TIME").toString();

		// 干扰矩阵和流量文件上传ID
	    matrixDcId = pciPlanRec.get("MATRIX_DATA_COLLECT_ID") == null ? 0
				: Long.parseLong(pciPlanRec.get("MATRIX_DATA_COLLECT_ID").toString());

		long flowDcId = pciPlanRec.get("FLOW_DATA_COLLECT_ID") == null ? 0
				: Long.parseLong(pciPlanRec.get("FLOW_DATA_COLLECT_ID").toString());
		
		int getMatrixType = Integer.parseInt(pciPlanRec.get("GET_MATRIX_TYPE").toString());

		this.filePath = pciPlanRec.get("RESULT_DIR").toString();
		this.redOutPath = filePath + "/out";
		this.fileName = pciPlanRec.get("RD_FILE_NAME").toString();
		this.planType = pciPlanRec.get("PLAN_TYPE").toString();
		this.schemeType = pciPlanRec.get("CONVER_TYPE").toString();
		this.isCheckNCell = "YES".equals(pciPlanRec.get("IS_CHECK_NCELL").toString().toUpperCase());
		this.isExportAssoTable = "YES".equals(pciPlanRec.get("IS_EXPORT_ASSOTABLE").toString().toUpperCase());
		this.isExportMidPlan = "YES".equals(pciPlanRec.get("IS_EXPORT_MIDPLAN").toString().toUpperCase());
		this.isExportNcCheckPlan = "YES".equals(pciPlanRec.get("IS_EXPORT_NCCHECKPLAN").toString().toUpperCase());
		this.fileNameList = Arrays.asList(null == pciPlanRec.get("SF_FILE_NAMES") ? new String[0]
				: pciPlanRec.get("SF_FILE_NAMES").toString().trim().split(","));

		// log.debug("fileNameList=" + fileNameList);
		if (!fileNameList.isEmpty() && !fileNameList.get(0).isEmpty()) {
			String inStr = "";
			for (String fn : fileNameList) {
				inStr += "'" + fn + "',";
			}
			inStr = inStr.substring(0, inStr.length() - 1);
			inStr = "(" + inStr + ")";
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("inStr", inStr);
			map.put("cityId", cityId);
			List<Map<String, Object>> res = oracleMapper.getSfFileInfo(map);
			for (Map<String, Object> m : res) {
				fn2Id.put(m.get("FILE_NAME").toString(), "fn*" + m.get("REC_ID") + "*fn");
			}
			isUseSf = true;
			this.d1D2Type = null == pciPlanRec.get("FREQ_ADJ_TYPE") ? "" : pciPlanRec.get("FREQ_ADJ_TYPE").toString();
			if (!d1D2Type.isEmpty()) {
				this.d1Freq = null == pciPlanRec.get("D1FREQ") ? "" : pciPlanRec.get("D1FREQ").toString();
				this.d2Freq = null == pciPlanRec.get("D2FREQ") ? "" : pciPlanRec.get("D2FREQ").toString();
			}
		}
		// log.debug("isUseSf=" + isUseSf);

		/************ Hadoop PCI干扰计算 start ************/

		// 获取页面自定义的阈值门限值
		List<RnoThreshold> rnoThresholds = new ArrayList<RnoThreshold>();
		List<Map<String, Object>> rawDatas = oracleMapper.queryParamInfo(jobId);

		if (rawDatas == null || rawDatas.size() == 0) {
			// 取默认门限值
			rawDatas = oracleMapper.queryDefaultParamInfo(jobId);
		}

		for (int i = 0; i < rawDatas.size(); i++) {
			Map<String, Object> map = rawDatas.get(i);
			String code = map.get("PARAM_CODE").toString();
			String val = map.get("PARAM_VAL").toString();

			RnoThreshold rnoThreshold = new RnoThreshold();
			rnoThreshold.setCode(code);
			rnoThreshold.setDefaultVal(val);
			rnoThresholds.add(rnoThreshold);
		}

		double samefreqcellcoefweight = 0.8; // 权值
		double switchratioweight = 0.2; // 切换比例权值
		double mincorrelation = 2;
		long minmeasuresum = 500;

		if (rnoThresholds != null) {
			for (RnoThreshold rnoThreshold : rnoThresholds) {
				String code = rnoThreshold.getCode();
				String val = rnoThreshold.getDefaultVal();
				if (code.equals("SAMEFREQCELLCOEFWEIGHT".toUpperCase())) {
					samefreqcellcoefweight = Double.parseDouble(val);
				}
				if (code.equals("SWITCHRATIOWEIGHT".toUpperCase())) {
					switchratioweight = Double.parseDouble(val);
				}
				if (code.equals("CELLM3RINTERFERCOEF".toUpperCase())) {
					this.m3r = Double.parseDouble(val);
				}
				if (code.equals("CELLM6RINTERFERCOEF".toUpperCase())) {
					this.m6r = Double.parseDouble(val);
				}
				if (code.equals("CELLM30RINTERFERCOEF".toUpperCase())) {
					this.m30r = Double.parseDouble(val);
				}
				if (code.equals("TOPNCELLLIST".toUpperCase())) {
					this.topRate = Double.parseDouble(val) * 0.01;
				}
				if (code.equals("CONVERMETHOD1TARGETVAL".toUpperCase())) {
					this.defInterRate = Double.parseDouble(val) * 0.01;
				}
				if (code.equals("CONVERMETHOD2TARGETVAL".toUpperCase())) {
					this.defVariance = Double.parseDouble(val) * 0.01;
				}
				if (code.equals("CONVERMETHOD2SCOREN".toUpperCase())) {
					this.divideNumber = Integer.parseInt(val);
				}
				if (code.equals("MINCORRELATION".toUpperCase())) {
					mincorrelation = Double.parseDouble(val);
				}
				if (code.equals("MINMEASURESUM".toUpperCase())) {
					minmeasuresum = Long.parseLong(val);
				}
				if (code.equals("DISLIMIT".toUpperCase())) {
					this.dislimit = Double.parseDouble(val);
				}
			}
		}

		logger.debug("门限值：" + "samefreqcellcoefweight=" + samefreqcellcoefweight + ",switchratioweight="
				+ switchratioweight + ",cellm3rinterfercoef=" + m3r + ",cellm6rinterfercoef=" + m6r
				+ ",cellm30rinterfercoef=" + m30r + ",topncelllist=" + topRate + ",convermethod1targetval="
				+ defInterRate + ",convermethod2targetval=" + defVariance + ",convermethod2scoren=" + divideNumber
				+ ",mincorrelation=" + mincorrelation + ",minmeasuresum=" + minmeasuresum + ",dislimit=" + dislimit);

		
		// 获取干扰矩阵导入计算信息
		if (getMatrixType == 1) {//采用矩阵文件
			// 获取job相关的信息
			Date now = new Date();
			List<Map<String, Object>> recs = oracleMapper.getmatrixInfo(matrixDcId);
			if (recs == null || recs.size() <= 0) {
				// 失败了
				msg = "未找到干扰矩阵文件！";
				logger.debug(msg);
				addReport(now, msg, DataParseStatus.Fail.toString(), "获取干扰矩阵文件");
				updateJobEndTime(job, JobParseStatus.Fail.toString());
				return false;
			}
			this.matrixDfPath = recs.get(0).get("FULL_PATH").toString();
		} else if (getMatrixType == 2) {//采用新计算的矩阵
			
			List<String> list = null;
			Map<String, Object> timeMap = new HashMap<>();
			Map<String, Object> dataMap = new HashMap<>();

			try {
		
				timeMap.put("startMeaDay", startMeaDate);
				timeMap.put("endMeaDay", endMeaDate);
				timeMap.put("areaId", cityId);
				dataMap.put("jobId", jobId);
				dataMap.put("areaId", cityId);
				
				//准备MR临时表数据
				list =  sparkMapper.queryMrMeaDate(timeMap);
				dataMap.put("meaTime", list);
				if(!list.isEmpty()){
					sparkMapper.createMrTempTable(dataMap);
				}
			    
			    //准备HO临时表数据
				list =  sparkMapper.queryHoMeaDate(timeMap);
				dataMap.put("meaTime", list);
				if(!list.isEmpty()){
					sparkMapper.createHoTempTable(dataMap);
				}
			    
			    //准备SF临时表数据
				if (isUseSf) {
					list = sparkMapper.querySfMeaDate(timeMap);
					dataMap.put("meaTime", list);
					if (!list.isEmpty()) {
						sparkMapper.createSfTempTable(dataMap);
					}
				}
				
				// 如果流量文件ID大于0 获取流量文件信息；
				if (flowDcId > 0) {
					// 获取修正值
					this.dtKs = pciPlanRec.get("KS_CORR_VAL") == null ? -1
							: Double.parseDouble(pciPlanRec.get("KS_CORR_VAL").toString());
					if (!(dtKs > 0)) {
						dtKs = -1;
					}
					
					list =  sparkMapper.queryFlowMeaDate(timeMap);
					dataMap.put("meaTime", list);
					if(!list.isEmpty()){
						sparkMapper.createFlowTempTable(dataMap);
					}
					List<Map<String, Object>> cellToFlow = sparkMapper.getFlow(dataMap);
					// 计算全网平均业务量
					double perFlow = 0;
					for (Map<String, Object> map : cellToFlow) {
						double one;
						if(map.get("flow")==null){
							one = 0;
						}else {
							one = Double.parseDouble(map.get("flow").toString());
						}
						perFlow += one < 100 ? 100 : one;
					}
					perFlow = perFlow / cellToFlow.size();

					for (Map<String, Object> map : cellToFlow) {
						double ks = 1;
						double one;
						if(map.get("flow")==null){
							one = 0;
						}else {
							one = Double.parseDouble(map.get("flow").toString());
						}
						double flow = one < 100 ? 100 : one;
						ks = flow / perFlow;
						cellToKs.put(map.get("cell_id").toString().intern(), ks);
					}
				}
				
			    //计算干扰矩阵并入到仓库
				Map<String, Object> map = new HashMap<>();
				map.put("jobId", jobId);
				map.put("samefreqcellcoefweight", samefreqcellcoefweight);
				map.put("switchratioweight", switchratioweight);
				map.put("dislimit", dislimit);
				map.put("rsrp0minus1weight", rsrp0minus1weight);
				map.put("rsrp1weight", rsrp1weight);
				map.put("minmeasuresum", minmeasuresum);
				map.put("mincorrelation", mincorrelation);
				map.put("startMeaDay", startMeaDate);
				map.put("endMeaDay", endMeaDate);
				map.put("areaId", cityId);
				if(isUseSf) {
					sparkMapper.createMatrix(map);
				} else {
					sparkMapper.createMatrixWithoutSf(map);		
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			
		}
		return true;
	}

	/**
	 * 从本地文件或者hive仓库中读取数据
	 */
	public boolean readData() {
		
		if (matrixDfPath != null) {
			
			File file = new File(matrixDfPath);
			if (file != null) {
				CSVParser parser = null;
				try {
						parser = new CSVParser(new BufferedReader(new FileReader(file)),
								CSVFormat.DEFAULT.withHeader());
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (parser != null) {
					String cellId;
					String ncellId;
					double assocDegree;
					for (CSVRecord record : parser) {
						try {
							cellId = record.get("小区标识");
							ncellId = record.get("邻区标识");
							assocDegree = Double.parseDouble(record.get("关联度"));
							if (!cellToNcellAssocDegree.containsKey(cellId)) {
								cellToNcellAssocDegree.put(cellId.intern(), new HashMap<String, Double>());
							}
							cellToNcellAssocDegree.get(cellId).put(ncellId.intern(), assocDegree);
							if (!ncellToCellAssocDegree.containsKey(ncellId)) {
								ncellToCellAssocDegree.put(ncellId.intern(), new HashMap<String, Double>());
							}
							ncellToCellAssocDegree.get(ncellId).put(cellId.intern(), assocDegree);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					try {
						parser.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					
				}
				
			}
			
		} else {
			
			List<Map<String, Object>> matrixList = sparkMapper.getMatrix(Integer.parseInt(matrixDcId+""));
			String cellId;
			String ncellId;
			double assocDegree;
			for(Map<String, Object> map : matrixList) {
				cellId = map.get("cell_id").toString();
				ncellId = map.get("ncell_id").toString();
				assocDegree = Double.parseDouble(map.get("rela_val").toString());
				if (!cellToNcellAssocDegree.containsKey(cellId)) {
					cellToNcellAssocDegree.put(cellId.intern(), new HashMap<String, Double>());
				}
				cellToNcellAssocDegree.get(cellId).put(ncellId.intern(), assocDegree);
				if (!ncellToCellAssocDegree.containsKey(ncellId)) {
					ncellToCellAssocDegree.put(ncellId.intern(), new HashMap<String, Double>());
				}
				ncellToCellAssocDegree.get(ncellId).put(cellId.intern(), assocDegree);
			}
			
		}

		if (!cellToNcellAssocDegree.isEmpty()) {
			cellToNcellAssocDegree.entrySet().stream().forEach(e -> cellToTotalAssocDegree.put(e.getKey(),
					e.getValue().values().stream().reduce(0d, Double::sum)));
			cellToTotalAssocDegree = sortMapByValue(cellToTotalAssocDegree);
			List<String> sstCells;
			List<String> nsstCells;
			// 邻区关联度从大到小排序
			for (String cell : cellToNcellAssocDegree.keySet()) {
				cellToNcellAssocDegree.put(cell.intern(), sortMapByValue(cellToNcellAssocDegree.get(cell)));

				sstCells = cellToSameStationOtherCells.get(cell);
				if (sstCells == null) {
					sstCells = new ArrayList<String>();
				}

				if (!cellToNotSameStationCells.containsKey(cell)) {
					nsstCells = new ArrayList<String>();
					for (String ncell : cellToNcellAssocDegree.get(cell).keySet()) {
						if (!sstCells.contains(ncell)
								&& cellToEarfcn.get(cell).equals(cellToEarfcn.get(ncell))) {
							nsstCells.add(ncell.intern());
						}
					}
					cellToNotSameStationCells.put(cell.intern(), nsstCells);
				}
			}
			// 求以邻区为key的反向映射
			for (String ncell : ncellToCellAssocDegree.keySet()) {
				ncellToCellAssocDegree.put(ncell.intern(), sortMapByValue(ncellToCellAssocDegree.get(ncell)));

				sstCells = cellToSameStationOtherCells.get(ncell);
				if (sstCells == null) {
					sstCells = new ArrayList<String>();
				}
				if (!ncellToNotSameStationCells.containsKey(ncell)) {
					nsstCells = new ArrayList<String>();
					for (String cell : ncellToCellAssocDegree.get(ncell).keySet()) {
						if (!sstCells.contains(cell)
								&& cellToEarfcn.get(ncell).equals(cellToEarfcn.get(cell))) {
							nsstCells.add(cell.intern());
						}
					}
					ncellToNotSameStationCells.put(ncell.intern(), nsstCells);
				}
			}
			logger.debug("cellToNcellAssocDegree.size()=" + cellToNcellAssocDegree.size());
			logger.debug("ncellToCellAssocDegree.size()=" + ncellToCellAssocDegree.size());
			logger.debug("cellToTotalAssocDegree.size()=" + cellToTotalAssocDegree.size());
			logger.debug("cellToSameStationOtherCells.size()=" + cellToSameStationOtherCells.size());
			logger.debug("cellToNotSameStationCells.size()=" + cellToNotSameStationCells.size());
			logger.debug("ncellToNotSameStationCells.size()=" + ncellToNotSameStationCells.size());
			logger.debug("cellToOriPci.size()=" + cellToOriPci.size());
			logger.debug("cellToLonLat.size()=" + cellToLonLat.size());
			logger.debug("cellToEarfcn.size()=" + cellToEarfcn.size());
			
		}
		// 读取d1d2小区数据
		if (!d1D2Type.isEmpty()) {
			List<Map<String, Object>> d1d2List = sparkMapper.getSFd1d2Cell(Integer.parseInt(jobId+""));
			Set<String> set = new HashSet<>();
			for(Map<String, Object> map : d1d2List) {
				if(set.add(map.get("cell_id").toString())) {
					abCellList.add(map.get("cell_id").toString());
				}
				if(set.add(map.get("ncell_id").toString())) {
					abCellList.add(map.get("ncell_id").toString());
				}
			}
			set = null;
			logger.debug("abCellList.size()=" + abCellList.size());
		}
		
		return true;
	}

	private Map<String, Double> sortMapByValue(Map<String, Double> unsortMap) {
		return unsortMap.entrySet().parallelStream().sorted((o1, o2) -> Double.compare(o2.getValue(), o1.getValue()))
				.collect(LinkedHashMap::new, (m, p) -> m.put(p.getKey(), p.getValue()), Map::putAll);
	}

	/**
	 * 拼装configuration传递的数据
	 * 
	 * @param cellList
	 * @return
	 */
	public static String map2String(List<String> cellList) {
		StringBuffer sb = new StringBuffer();
		for (String key : cellList) {
			sb.append(key).append("#");
		}
		return sb.substring(0, sb.length() - 1);
	}

	/**
	 * 添加报告到数据库
	 */
	private void addReport(Date date1, String msg, String status, String stage) {
		Date date2;
		Report report = new Report();
		date2 = new Date();
		report.setJobId(jobId);
		report.setBegTime(date1);
		report.setEndTime(date2);
		report.setFinishState(status);
		report.setStage(stage);
		report.setAttMsg(msg);
		oracleMapper.addReport(report);
	}

	/**
	 * 更新任务状态
	 */
	private void updateJobEndTime(JobProfile job, String jobStatus) {
		job.setJobStateStr(jobStatus);
		job.setFinishTime(new Date());
		oracleMapper.updateJobEndTime(job);
	}
}
