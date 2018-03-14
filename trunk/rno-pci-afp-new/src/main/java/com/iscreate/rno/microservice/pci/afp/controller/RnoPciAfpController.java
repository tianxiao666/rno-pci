package com.iscreate.rno.microservice.pci.afp.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Paths;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.iscreate.rno.microservice.pci.afp.mapper.oracle.OracleMapper;
import com.iscreate.rno.microservice.pci.afp.model.Area;
import com.iscreate.rno.microservice.pci.afp.model.G4SfDescQueryCond;
import com.iscreate.rno.microservice.pci.afp.model.JobParseStatus;
import com.iscreate.rno.microservice.pci.afp.model.JobProfile;
import com.iscreate.rno.microservice.pci.afp.model.Page;
import com.iscreate.rno.microservice.pci.afp.model.RnoDataCollectRec;
import com.iscreate.rno.microservice.pci.afp.model.RnoLteInterferCalcTask;
import com.iscreate.rno.microservice.pci.afp.model.RnoThreshold;
import com.iscreate.rno.microservice.pci.afp.model.TaskCond;
import com.iscreate.rno.microservice.pci.afp.properties.RnoProperties;
import com.iscreate.rno.microservice.pci.afp.service.RnoCommonService;
import com.iscreate.rno.microservice.pci.afp.service.RnoJobService;
import com.iscreate.rno.microservice.pci.afp.service.RnoPciAfpService;
import com.iscreate.rno.microservice.pci.afp.tool.DateUtil;
import com.iscreate.rno.microservice.pci.afp.tool.FileTool;
import com.iscreate.rno.microservice.pci.afp.tool.ZipFileHandler;

@RestController
public class RnoPciAfpController {

	private static final Logger logger = LoggerFactory.getLogger(RnoPciAfpController.class);

	@Autowired
	@Qualifier("rnoCommonServiceImpl")
	private RnoCommonService rnoCommonService;

	@Autowired
	@Qualifier("rnoPciAfpServiceImpl")
	private RnoPciAfpService rnoPciAfpService;

	@Autowired
	private OracleMapper oracleMapper;

	@Autowired
	private RnoProperties rnoProperties;

	@Autowired
	@Qualifier("rnoJobServiceImpl")
	private RnoJobService rnoJobService;

	@Autowired
	private MultipartConfigElement multipartConfigElement;

	private List<Area> provincesList = new ArrayList<Area>();

	private List<Area> citiesList = new ArrayList<Area>();

	private List<Area> allAreas;

	private RnoLteInterferCalcTask taskobj = new RnoLteInterferCalcTask();

	private Map<String, String> threshold = new HashMap<String, String>();

	private List<RnoThreshold> rnoThresholds;

	private List<Map<String, String>> list;

	private long tempJobId;

	/**
	 * 获取用户权限下的区域
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping("/")
	@ResponseStatus(HttpStatus.OK)
	public ModelAndView index(Map<String, Object> model, HttpServletRequest request, String account) {
	
		if(account==null || "".equals(account)){
			Object obj = request.getSession().getAttribute("account");
			if(obj != null){
				account = obj.toString();
			}else{
				model.put("error", "非法登录用户，请选择正常渠道登录，谢谢！");
				return new ModelAndView("rno_fail");
			}
		}else {
			request.getSession().setAttribute("account", account);
		}
		
		logger.debug("访问首页,account = {}", account);
		
		Map<String, List<Area>> map = null;
		try {
			map = rnoCommonService.getAreaByAccount(account);
		} catch (Exception e) {
			model.put("error", "当前用户没有权限！");
			return new ModelAndView("rno_fail");
		}
		provincesList = map.get("provinceAreas");
		citiesList = map.get("cityAreas");
		allAreas = map.get("allAreas");
		model.put("provinces", provincesList);
		model.put("cities", citiesList);
		logger.debug("provinceAreas={}, city={}, allAreas={}", provincesList, citiesList, allAreas);
		return new ModelAndView("rno_lte_interfer_calc_new");
	}

	@RequestMapping("/addTask_{cityId}")
	public ModelAndView addTask(Map<String, Object> model, @PathVariable("cityId") long cityId) {
		logger.debug("进入addTask方法");
		List<Map<String, Object>> list = rnoPciAfpService.getLatelyLteMatrixByCityId(cityId);
		model.put("provinces", provincesList);
		model.put("cities", citiesList);
		model.put("allAreas", allAreas);
		model.put("matrix", list);
		return new ModelAndView("rno_lte_interfer_calc_taskinfo_new");
	}

	@RequestMapping("/returnBack")
	public ModelAndView returnBack(Map<String, Object> model) {
		logger.debug("进入returnBack方法");
		model.put("provinces", provincesList);
		model.put("cities", citiesList);
		model.put("allAreas", allAreas);
		return new ModelAndView("rno_lte_interfer_calc_new");
	}
	
	/**
	 * 获取最近十次 LTE 干扰矩阵信息
	 */
	@RequestMapping("/getLatelyLteMatrix/{cityId}")
	List<Map<String, Object>> getLatelyLteMatrixByCityIdForAjaxAction(@PathVariable("cityId") long cityId) {
		logger.debug("进入getLatelyLteMatrixByCityIdForAjaxAction方法,cityId={}", cityId);
		return rnoPciAfpService.getLatelyLteMatrixByCityId(cityId);
	}

	@RequestMapping("/taskInfoForward_{isNewMatrix}")
	public ModelAndView taskInfoForward(Map<String, Object> model, @PathVariable("isNewMatrix") String isNewMatrix) {
		logger.debug("进入taskInfoForward方法");

	    rnoThresholds = rnoPciAfpService.getThresholdsByModuleType("LTEINTERFERCALC");
	    if(isNewMatrix.equals("NO")){
	    	for(RnoThreshold r : rnoThresholds){
	    		String code = r.getCode();
	    		if(code.equals("SAMEFREQCELLCOEFWEIGHT")||code.equals("SWITCHRATIOWEIGHT")||code.equals("MINMEASURESUM")
	    		||code.equals("MINCORRELATION")||code.equals("DISLIMIT")) {
	    			r.setFlag(false);
	    		}
	    	}
	    } else {
	    	taskobj.getTaskInfo().setGetMatrixType(2);
	    }
		taskobj.setRnoThresholds(rnoThresholds);

		// 获取不重复的分组条件
		HashSet<String> conditionGroups = new HashSet<String>();
		for (RnoThreshold one : rnoThresholds) {
			conditionGroups.add(one.getConditionGroup());
		}

		Map<Long, List<RnoThreshold>> groupRnoThresholds = new TreeMap<Long, List<RnoThreshold>>();
		List<RnoThreshold> groupRnoThreshold;
		long orderNum = 0;
		// 循环分组条件，加入参数对象
		for (String cond : conditionGroups) {
			groupRnoThreshold = new ArrayList<RnoThreshold>();
			for (RnoThreshold rnoThreshold : rnoThresholds) {
				if (cond.equals(rnoThreshold.getConditionGroup())) {
					groupRnoThreshold.add(rnoThreshold);
					orderNum = rnoThreshold.getOrderNum();
				}
			}
			groupRnoThresholds.put(orderNum, groupRnoThreshold);
		}

		taskobj.setGroupThresholds(groupRnoThresholds); // 用于分组显示
		model.put("rnoThresholds", rnoThresholds);
		logger.debug("rnoThresholds={}", rnoThresholds);
		return new ModelAndView("rno_lte_interfer_calc_paraminfo_new");
	}

	@RequestMapping("/paramInfoBack")
	public ModelAndView paramInfoBack(Map<String, Object> model) {
		logger.debug("进入paramInfoBack方法");
		long cityId = taskobj.getTaskInfo().getCityId();
		List<Map<String, Object>> list = rnoPciAfpService.getLatelyLteMatrixByCityId(cityId);
		model.put("provinces", provincesList);
		model.put("cities", citiesList);
		model.put("allAreas", allAreas);
		model.put("matrix", list);
		return new ModelAndView("rno_lte_interfer_calc_taskinfo_new");
	}

	@RequestMapping("/paramInfoForward")
	public ModelAndView paramInfoForward(Map<String, Object> model, HttpServletRequest request) {
		logger.debug("进入paramInfoForward方法");
		String account = "";
		if (request.getParameter("account") != null) {
			account = request.getParameter("account").toString();
			request.getSession().setAttribute("userId", account);
		} else {
			model.put("error", "非法登录用户，请选择正常渠道登录，谢谢！");
			return new ModelAndView("rno_fail");
		}

		if (taskobj.getTaskInfo().getIsUseSf()) {
			G4SfDescQueryCond cond = new G4SfDescQueryCond();
			cond.setCityId(taskobj.getTaskInfo().getCityId());
			cond.setMeaBegTime(taskobj.getTaskInfo().getStartTime());
			cond.setMeaEndTime(taskobj.getTaskInfo().getEndTime());
			cond.setFactory("ALL");
			list = rnoPciAfpService.querySfDataFromHbaseByPage(cond);
			taskobj.setSfFileInfo(list);
			model.put("sfFileInfo", list);
		}
		model.put("taskobj", taskobj.getTaskInfo());
		logger.debug("taskInfo.getTaskInfo={}",taskobj.getTaskInfo());
		return new ModelAndView("rno_lte_interfer_calc_overviewinfo_new");
	}

	@RequestMapping("/overviewInfoBack")
	public ModelAndView overviewInfoBack(Map<String, Object> model) {
		logger.debug("进入overviewInfoBack方法");
		model.put("taskobj", taskobj.getTaskInfo());
		model.put("rnoThresholds", rnoThresholds);
		return new ModelAndView("rno_lte_interfer_calc_paraminfo_new");
	}

	
	@RequestMapping("/querySubAreasByParentId")
	public Map<String, Object> querySubAreasByParentId(String account, Integer parentId) {
		logger.debug("进入querySubAreasByParentId方法.参数：account={},parentId={}", account, parentId);
		Map<String, Object> res = new HashMap<>();
		if (allAreas == null) {
			Map<String, List<Area>> map = rnoCommonService.getAreaByAccount(account);
			provincesList = map.get("provinceAreas");
			citiesList = map.get("cityAreas");
			allAreas = map.get("allAreas");
		}
		if (allAreas != null && parentId != null) {
			List<Area> subAreas = allAreas.parallelStream().filter(e -> e.getParentId() == parentId)
					.collect(Collectors.toList());
			res.put("data", subAreas);
			res.put("state", true);
		} else {
			res.put("data", new ArrayList<>());
			res.put("state", false);
		}
		return res;
	}

	@RequestMapping("/storageTaskInfoForward")
	public Map<String, Object> storageTaskInfoForward(HttpServletRequest request) {
		logger.debug("进入storageTaskInfoForward方法.isImportMatrix={}",request.getParameter("isImportMatrix"));
		Map<String, Object> res = new HashMap<>();
		try {
			// 仅仅是存储任务消息内容
			RnoLteInterferCalcTask.TaskInfo ti = taskobj.getTaskInfo();
			ti.setTaskName(request.getParameter("taskName"));
			ti.setTaskDesc(request.getParameter("taskDescription"));
			ti.setStartTime(request.getParameter("meaStartTime").replace("-", ""));
			ti.setEndTime(request.getParameter("meaEndTime").replace("-", ""));
			ti.setProvinceId(Long.parseLong(request.getParameter("provinceId")));
			ti.setCityId(Long.parseLong(request.getParameter("cityId")));
			ti.setCityName(request.getParameter("cityName").trim());
			ti.setProvinceName(request.getParameter("provinceName").trim());
			ti.setPlanType(request.getParameter("planType"));
			ti.setConverType(request.getParameter("converType"));
			ti.setRelaNumerType(request.getParameter("cosi"));
			ti.setIsCheckNCell(request.getParameter("isCheckNCell"));
			ti.setIsExportAssoTable(request.getParameter("isExportAssoTable"));
			ti.setIsExportMidPlan(request.getParameter("isExportMidPlan"));
			ti.setIsExportNcCheckPlan(request.getParameter("isExportNcCheckPlan"));
			ti.setIsImportMatrix(request.getParameter("isImportMatrix"));
			ti.setMatrixDataCollectId(Long.parseLong(request.getParameter("matrixJobId")));
			if(request.getParameter("isUseFlow").equals("YES")){
				ti.setIsUseFlow(true);
				ti.setFlowDataCollectId(1);
				ti.setKs(Double.parseDouble(request.getParameter("ks")));
			}
			if(request.getParameter("isImportMatrix").equals("YES")) {
				ti.setGetMatrixType(1);
			}
			if(request.getParameter("isUseSf").equals("YES")){
				ti.setIsUseSf(true);
				if (request.getParameter("freqAdjType") != null && !"".equals(request.getParameter("freqAdjType"))
						&& !"undefined".equals(request.getParameter("freqAdjType").toLowerCase())) {
					ti.setFreqAdjType(request.getParameter("freqAdjType"));
					ti.setD1Freq(request.getParameter("d1Freq"));
					ti.setD2Freq(request.getParameter("d2Freq"));
				} else {
					ti.setFreqAdjType("");
				}
			}
			logger.debug("ti={}",ti.toString());
			res.put("state", true);
		} catch (Exception e) {
			res.put("state", false);
			e.printStackTrace();
		}
		return res;
	}
     
	@RequestMapping("/storageParamInfoBack")
	public Map<String, Object> storageParamInfoBack(HttpServletRequest request) {
		threshold.put("SAMEFREQCELLCOEFWEIGHT", request.getParameter("SAMEFREQCELLCOEFWEIGHT"));
		threshold.put("SWITCHRATIOWEIGHT", request.getParameter("SWITCHRATIOWEIGHT"));
		threshold.put("CELLM3RINTERFERCOEF", request.getParameter("CELLM3RINTERFERCOEF"));
		threshold.put("CELLM6RINTERFERCOEF", request.getParameter("CELLM6RINTERFERCOEF"));
		threshold.put("CELLM30RINTERFERCOEF", request.getParameter("CELLM30RINTERFERCOEF"));
		threshold.put("BEFORENSTRONGCELLTAB", request.getParameter("BEFORENSTRONGCELLTAB"));
		threshold.put("TOPNCELLLIST", request.getParameter("TOPNCELLLIST"));
		threshold.put("INCREASETOPNCELLLIST", request.getParameter("INCREASETOPNCELLLIST"));
		threshold.put("CONVERMETHOD1TARGETVAL", request.getParameter("CONVERMETHOD1TARGETVAL"));
		threshold.put("CONVERMETHOD2TARGETVAL", request.getParameter("CONVERMETHOD2TARGETVAL"));
		threshold.put("CONVERMETHOD2SCOREN", request.getParameter("CONVERMETHOD2SCOREN"));
		threshold.put("MINCORRELATION", request.getParameter("MINCORRELATION"));
		threshold.put("MINMEASURESUM", request.getParameter("MINMEASURESUM"));
		threshold.put("DISLIMIT", request.getParameter("DISLIMIT"));
		Map<String, Object> res = new HashMap<>();
		try {
		  rnoThresholds = taskobj.getRnoThresholds();
			for (RnoThreshold rnoThreshold : rnoThresholds) {
				String code = rnoThreshold.getCode();
				for (String key : threshold.keySet()) {
					if (key.toUpperCase().equals(code)) {
						rnoThreshold.setDefaultVal(threshold.get(key));
					}
				}
			}
			// 获取不重复的分组条件
			HashSet<String> conditionGroups = new HashSet<String>();
			for (RnoThreshold one : rnoThresholds) {
				conditionGroups.add(one.getConditionGroup());
			}
			Map<Long, List<RnoThreshold>> groupRnoThresholds = new TreeMap<Long, List<RnoThreshold>>();
			long orderNum = 0;
			List<RnoThreshold> groupRnoThreshold;
			// 循环分组条件，加入参数对象
			for (String cond : conditionGroups) {
				groupRnoThreshold = new ArrayList<RnoThreshold>();
				for (RnoThreshold rnoThreshold : rnoThresholds) {
					if (cond.equals(rnoThreshold.getConditionGroup())) {
						groupRnoThreshold.add(rnoThreshold);
						orderNum = rnoThreshold.getOrderNum();
					}
				}
				groupRnoThresholds.put(orderNum, groupRnoThreshold);
			}
			taskobj.setGroupThresholds(groupRnoThresholds);

			res.put("state", true);
		} catch (Exception e) {
			res.put("state", false);
			e.printStackTrace();
		}
		return res;
	}

	@RequestMapping("/storageParamInfoForward")
	public Map<String, Object> storageParamInfoForward(HttpServletRequest request) {

		threshold.put("SAMEFREQCELLCOEFWEIGHT", request.getParameter("SAMEFREQCELLCOEFWEIGHT"));
		threshold.put("SWITCHRATIOWEIGHT", request.getParameter("SWITCHRATIOWEIGHT"));
		threshold.put("CELLM3RINTERFERCOEF", request.getParameter("CELLM3RINTERFERCOEF"));
		threshold.put("CELLM6RINTERFERCOEF", request.getParameter("CELLM6RINTERFERCOEF"));
		threshold.put("CELLM30RINTERFERCOEF", request.getParameter("CELLM30RINTERFERCOEF"));
		threshold.put("BEFORENSTRONGCELLTAB", request.getParameter("BEFORENSTRONGCELLTAB"));
		threshold.put("TOPNCELLLIST", request.getParameter("TOPNCELLLIST"));
		threshold.put("INCREASETOPNCELLLIST", request.getParameter("INCREASETOPNCELLLIST"));
		threshold.put("CONVERMETHOD1TARGETVAL", request.getParameter("CONVERMETHOD1TARGETVAL"));
		threshold.put("CONVERMETHOD2TARGETVAL", request.getParameter("CONVERMETHOD2TARGETVAL"));
		threshold.put("CONVERMETHOD2SCOREN", request.getParameter("CONVERMETHOD2SCOREN"));
		threshold.put("MINCORRELATION", request.getParameter("MINCORRELATION"));
		threshold.put("MINMEASURESUM", request.getParameter("MINMEASURESUM"));
		threshold.put("DISLIMIT", request.getParameter("DISLIMIT"));
		Map<String, Object> res = new HashMap<>();
		try {
			List<RnoThreshold> rnoThresholds = taskobj.getRnoThresholds();
			for (RnoThreshold rnoThreshold : rnoThresholds) {
				String code = rnoThreshold.getCode();
				for (String key : threshold.keySet()) {
					if (key.toUpperCase().equals(code)) {
						rnoThreshold.setDefaultVal(threshold.get(key));
					}
				}
			}
			// 获取不重复的分组条件
			HashSet<String> conditionGroups = new HashSet<String>();
			for (RnoThreshold one : rnoThresholds) {
				conditionGroups.add(one.getConditionGroup());
			}
			Map<Long, List<RnoThreshold>> groupRnoThresholds = new TreeMap<Long, List<RnoThreshold>>();
			long orderNum = 0;
			List<RnoThreshold> groupRnoThreshold;
			// 循环分组条件，加入参数对象
			for (String cond : conditionGroups) {
				groupRnoThreshold = new ArrayList<RnoThreshold>();
				for (RnoThreshold rnoThreshold : rnoThresholds) {
					if (cond.equals(rnoThreshold.getConditionGroup())) {
						groupRnoThreshold.add(rnoThreshold);
						orderNum = rnoThreshold.getOrderNum();
					}
				}
				groupRnoThresholds.put(orderNum, groupRnoThreshold);
			}
			taskobj.setGroupThresholds(groupRnoThresholds);
			taskobj.setRnoThresholds(rnoThresholds);
			res.put("state", true);
		} catch (Exception e) {
			res.put("state", false);
			e.printStackTrace();
		}
		return res;
	}

	@RequestMapping("/storageOverViewBack")
	public Map<String, Object> storageOverViewBack(HttpServletRequest request) {
		Map<String, Object> res = new HashMap<>();
		try {
			// 缓存需要优化的小区列
			taskobj.getTaskInfo().setLteCells(request.getParameter("lteCells"));
			res.put("state", true);
		} catch (Exception e) {
			res.put("state", false);
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * 查询pci自动规划任务
	 * 
	 */
	
	@RequestMapping("/queryNewPciPlanAnalysisTaskByPage")
	public Map<String, Object> queryNewPciPlanAnalysisTaskByPage(Page page, TaskCond cond) {
		logger.debug("进入：queryNewPciPlanAnalysisTaskByPageForAjaxAction。参数：{}", cond);
		List<Map<String, Object>> pciTasks = rnoPciAfpService.queryPciPlanTaskByPage(cond, page);

		int totalCnt = page.getTotalCnt();
		page.setTotalPageCnt(totalCnt / page.getPageSize() + (totalCnt % page.getPageSize() == 0 ? 0 : 1));
		page.setForcedStartIndex(-1);

		Map<String, Object> res = new HashMap<>();
		res.put("page", page);
		res.put("data", pciTasks);

		return res;
	}
  
	/**
	 * 终止Pci规划任务
	 * 
	 */
	@RequestMapping("/stopNewPciJobByJobIdAndMrJobId")
	public Map<String, Object> stopNewPciJobByJobIdAndMrJobId(HttpServletRequest request) {
		logger.debug("进入方法：stopNewPciJobByJobIdAndMrJobIdForAjaxAction。jobId={}, mrJobId={}",
				Long.parseLong(request.getParameter("jobId")), request.getParameter("mrJobId"));
		//String account = request.getParameter("account").toString();
		boolean flag = rnoPciAfpService.stopJob(Long.parseLong(request.getParameter("jobId")));
		Map<String, Object> res = new HashMap<String, Object>();
		res.put("flag", flag);
		return res;
	}

	/**
	 * 查询指定job的报告
	 * 
	 */
	@RequestMapping("/queryJobReport")
	public Map<String, Object> queryJobReport(@RequestParam(required = false, defaultValue = "-1") Long jobId,
			Page page) {
		logger.debug("queryJobReportAjaxAction.jobId={}", jobId);

		Map<String, Object> res = new HashMap<String, Object>();

		if (jobId < 0) {
			logger.error("未传入一个有效的jobid！无法查看其报告！");
			page.setTotalCnt(0);
			page.setTotalPageCnt(0);
			res.put("page", page);
			res.put("data", new ArrayList<Map<String, Object>>());
			logger.debug("退出queryJobReportAjaxAction。输出：{}", res);
			return res;
		}

		int cnt = 0;
		if (page.getTotalCnt() < 0) {
			cnt = rnoCommonService.queryJobReportCnt(jobId);
			page.setTotalCnt(cnt);
		}

		Map<String, Object> map = new HashMap<String, Object>();
		int start = (page.getPageSize() * (page.getCurrentPage() - 1) + 1);
		int end = (page.getPageSize() * page.getCurrentPage());
		map.put("jobId", jobId);
		map.put("start", start);
		map.put("end", end);

		List<Map<String, Object>> reportRecs = rnoCommonService.queryJobReportByPage(map);
		logger.debug("reportRecs size:{}", reportRecs == null ? 0 : reportRecs.size());

		int totalCnt = page.getTotalCnt();
		page.setTotalPageCnt(totalCnt / page.getPageSize() + (totalCnt % page.getPageSize() == 0 ? 0 : 1));
		page.setForcedStartIndex(-1);

		res.put("page", page);
		res.put("data", reportRecs);
		logger.debug("退出queryJobReportAjaxAction。输出：{}", res);
		return res;
	}

	/**
	 * 下载PCI结果文件
	 * 
	 * @throws SQLException
	 */
	@RequestMapping("/downloadPciFile")
	public ResponseEntity<byte[]> downloadPciFile(HttpServletRequest request) {

		long jobId = Long.parseLong(request.getParameter("jobId"));

		logger.debug("下载PCI规划结果文件， jobId={}, mrJobId={}", request.getParameter("jobId"),
				request.getParameter("mrJobId"));

		String error = "";
		// 获取任务的信息。
		List<Map<String, Object>> pciTaskInfo = oracleMapper.getTaskInfoByJobId(jobId);

		if (pciTaskInfo.size() <= 0) {
			error = "不存在该" + jobId + "任务信息";
			logger.info(error);
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		// 区域ID
		long cityId = Long.parseLong(pciTaskInfo.get(0).get("CITY_ID").toString());
		Map<String, List<String>> cellIdToCessInfo = rnoPciAfpService.getLteCellInfoByCellId(cityId);

		if (cellIdToCessInfo.size() <= 0) {
			error = "该区域" + cityId + "不存在系统小区信息";
			logger.info(error);
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		// 获取pci规划任务中的待优化小区字段
		Clob cellsClob = (Clob) pciTaskInfo.get(0).get("OPTIMIZE_CELLS");
		Map<String, String> cellsMap = new HashMap<String, String>();
		try {
			String[] cellStrs = cellsClob.getSubString(1, (int) cellsClob.length()).trim().split(",");
			for (String c : cellStrs) {
				if (!"".equals(c.trim())) {
					cellsMap.put(c, "");
				}
			}
		} catch (SQLException e3) {
			e3.printStackTrace();
		}

		List<String> sourceFileList = new ArrayList<String>();
		String result = "success";

		// 下载的Pci规划文件全路径
		Calendar calendar = new GregorianCalendar();
		Date createDate = new DateUtil().parseDateArbitrary(pciTaskInfo.get(0).get("CREATE_TIME").toString());
		calendar.setTime(createDate);
		String path = rnoProperties.getDownloadFilePath();
		String dlFileRealdir = path + "/" + calendar.get(Calendar.YEAR) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/"
				+ jobId + "/";
		// 下载的Pci规划文件名称
		String dlFileName = "";
		// 下载的Pci规划文件全路径
		String dlFileRealPath = "";
		if ("success".equals(result) && pciTaskInfo.get(0).get("IS_EXPORT_ASSOTABLE").toString().equals("YES")) {
			// System.out.println("进来关联表生成方法");
			// 下载的Pci规划文件名称
			dlFileName = jobId + "_PCI优化关联度排序表.xlsx";
			dlFileRealPath = dlFileRealdir + dlFileName;
			// System.out.println("sourceFileList:"+sourceFileList);
			File realdir = new File(dlFileRealdir);
			if (!realdir.exists()) {
				realdir.mkdirs();
			}
			File realfile = new File(dlFileRealPath);
			if (!realfile.exists()) {
				try {
					realfile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			try {
				List<Map<String, Object>> res = oracleMapper.getAssoTable(jobId);
				// 可以生成excel结果文件
				if (FileTool.createAssoTableToExcel(dlFileRealPath, res)) {
					sourceFileList.add(dlFileRealPath);
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.info("获取Pci规划结果源文件中，读取关联表数据出错！");
				result = "error";
			}
		}
		if ("success".equals(result) && pciTaskInfo.get(0).get("IS_EXPORT_MIDPLAN").toString().equals("YES")) {
			// System.out.println("进来中间方案生成方法");
			int DataNum = oracleMapper.getMidPlanCount(jobId);
			//logger.debug("DataNum={}", DataNum);
			if ("success".equals(result) && DataNum >= 1) {
				for (int n = 1; n <= DataNum; n++) {
					// 下载的Pci规划文件名称
					dlFileName = jobId + "_PCI优化中间方案_" + n + ".xlsx";
					// 下载的Pci规划文件全路径
					dlFileRealPath = dlFileRealdir + dlFileName;

					// System.out.println("sourceFileList:"+sourceFileList);
					File realdir1 = new File(dlFileRealdir);
					if (!realdir1.exists()) {
						realdir1.mkdirs();
					}
					File realfile1 = new File(dlFileRealPath);
					if (!realfile1.exists()) {
						try {
							realfile1.createNewFile();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}

					List<Map<String, Object>> res1 = new ArrayList<Map<String, Object>>();
					List<Map<String, Object>> res2 = new ArrayList<Map<String, Object>>();
					Map<String, Object> one1 = null;
					Map<String, Object> one2 = null;

					String cellId = "";
					List<String> cells = null;
					try {
						List<Map<String, Object>> midPlanRes = oracleMapper.getMidPlanByPlanNum(jobId, n);
						List<Map<String, Object>> topCellRes = oracleMapper.getTopCell(jobId, n);
						
						for (Map<String, Object> map : midPlanRes) {
							one1 = new HashMap<String, Object>();
							cellId = map.get("CELL_ID").toString();

							cells = cellIdToCessInfo.get(cellId);
							if (cells == null) {
								logger.info("小区找不到对应工参数据：cellId ={}", cellId);
								one1.put("oldPci", -1);
								one1.put("oldEarfcn", -1);
								one1.put("cellName", "未知小区");
							} else {
								// 小区名，pci,频点
								one1.put("cellName", cells.get(0));
								one1.put("oldPci", Integer.parseInt(cells.get(1)));
								one1.put("oldEarfcn", Integer.parseInt(cells.get(2)));
							}
							one1.put("cellId", cellId);
							int newEarfcn = Integer.parseInt(map.get("EARFCN").toString());
							if (newEarfcn == -1) {
								if (cells == null) {
									one1.put("newEarfcn", "找不到对应工参数据");
								} else {
									one1.put("newEarfcn", "找不到对应MR数据");
								}
							} else {
								one1.put("newEarfcn", newEarfcn);
							}
							int newPci = Integer.parseInt(map.get("PCI").toString());
							if (newPci == -1) {
								if (cells == null) {
									one1.put("newPci", "找不到对应工参数据");
								} else {
									one1.put("newPci", "找不到对应MR数据");
								}
							} else {
								one1.put("newPci", newPci);
							}
							one1.put("oriInterVal", Double.parseDouble(map.get("OLD_INTER_VAL").toString()));
							one1.put("interVal", Double.parseDouble(map.get("NEW_INTER_VAL").toString()));

							if (cellsMap.containsKey(cellId)) {
								one1.put("remark", "修改小区");
							} else {
								one1.put("remark", "MR其他小区");
							}
							res1.add(one1);
						}
						for (Map<String, Object> map : topCellRes) {
							one2 = new HashMap<String, Object>();
							one2.put("cell", map.get("CELL_ID"));
							one2.put("inter", map.get("INTER_VAL"));
							res2.add(one2);
						}
						// 可以生成excel结果文件
						if (FileTool.createMidPlanToExcel(dlFileRealPath, res1, res2)) {
							sourceFileList.add(dlFileRealPath);
						}

					} catch (Exception e) {
						e.printStackTrace();
						logger.info("获取Pci规划结果源文件中，读取中间方案数据出错");
						result = "error";
					}

					if (result.equals("error")) {
						return new ResponseEntity<>(HttpStatus.NOT_FOUND);
					}
				}
			}
		}
		if ("success".equals(result) && pciTaskInfo.get(0).get("IS_EXPORT_NCCHECKPLAN").toString().equals("YES")) {
			// System.out.println("进来邻区核查方案生成方法");
			// 下载的Pci规划文件名称
			dlFileName = jobId + "_PCI优化邻区核查方案.xlsx";

			// 下载的Pci规划文件全路径
			dlFileRealPath = dlFileRealdir + dlFileName;
			// System.out.println("sourceFileList:"+sourceFileList);
			File realdir1 = new File(dlFileRealdir);
			if (!realdir1.exists()) {
				realdir1.mkdirs();
			}
			File realfile1 = new File(dlFileRealPath);
			if (!realfile1.exists()) {
				try {
					realfile1.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			List<Map<String, Object>> res1 = new ArrayList<Map<String, Object>>();
			Map<String, Object> one1 = null;
			String cellId = "";
			List<String> cells = null;
			try {
				List<Map<String, Object>> ncCheckRes = oracleMapper.getNcCheckPlan(jobId);
				
				for (Map<String, Object> map : ncCheckRes) {
					one1 = new HashMap<String, Object>();
					cellId = map.get("CELL_ID").toString();
					cells = cellIdToCessInfo.get(cellId);
					if (cells == null) {
						logger.info("小区找不到对应工参数据：cellId ={}", cellId);
						one1.put("cellName", "未知小区");
						one1.put("oldPci", -1);
						one1.put("oldEarfcn", -1);
					} else {
						// 小区名，pci,频点
						one1.put("cellName", cells.get(0));
						one1.put("oldPci", Integer.parseInt(cells.get(1)));
						one1.put("oldEarfcn", Integer.parseInt(cells.get(2)));
					}
					one1.put("cellId", cellId);
					int newEarfcn = Integer.parseInt(map.get("EARFCN").toString());
					if (newEarfcn == -1) {
						if (cells == null) {
							one1.put("newEarfcn", "找不到对应工参数据");
						} else {
							one1.put("newEarfcn", "找不到对应MR数据");
						}
					} else {
						one1.put("newEarfcn", newEarfcn);
					}
					int newPci = Integer.parseInt(map.get("PCI").toString());
					if (newPci == -1) {
						if (cells == null) {
							one1.put("newPci", "找不到对应工参数据");
						} else {
							one1.put("newPci", "找不到对应MR数据");
						}
					} else {
						one1.put("newPci", newPci);
					}
					one1.put("oriInterVal", Double.parseDouble(map.get("OLD_INTER_VAL").toString()));
					one1.put("interVal", Double.parseDouble(map.get("NEW_INTER_VAL").toString()));

					if (cellsMap.containsKey(cellId)) {
						one1.put("remark", "修改小区");
					} else {
						one1.put("remark", "MR其他小区");
					}
					res1.add(one1);
				}
				// 可以生成excel结果文件
				if (FileTool.createExcelFile(dlFileRealPath, res1)) {
					sourceFileList.add(dlFileRealPath);
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.info("获取Pci规划结果源文件中，读取邻区核查数据出错");
				result = "error";
			}
		}
		if ("success".equals(result) && pciTaskInfo.get(0).get("SF_FILE_NAMES") != null
				&& !"".equals(pciTaskInfo.get(0).get("SF_FILE_NAMES").toString())) {
			// System.out.println("进来邻区核查方案生成方法");
			// 下载的Pci规划文件名称
			dlFileName = jobId + "_D1小区表.csv";
			// 下载的Pci规划文件全路径
			dlFileRealPath = dlFileRealdir + dlFileName;
			String d1Path = dlFileRealPath;

			// System.out.println("sourceFileList:"+sourceFileList);
			File realdir = new File(dlFileRealdir);
			if (!realdir.exists()) {
				realdir.mkdirs();
			}
			File realfile = new File(dlFileRealPath);
			if (!realfile.exists()) {
				try {
					realfile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			String line = "";
			BufferedWriter bw = null;

			try {
				List<Map<String, Object>> d1CellRes = oracleMapper.getD1Cell(jobId);
				bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dlFileRealPath), "GBK"));
				// 标题头
				line = "小区标识," + "频点," + "干扰值";
				bw.write(line);
				bw.newLine();
				for (Map<String, Object> map : d1CellRes) {
					bw.write(map.get("CELL_ID") + "," + map.get("EARFCN") + "," + map.get("INTER_VAL"));
					bw.newLine();
				}
			} catch (IOException e) {
				logger.info("Pci规划计算的d1频数据不存在!");
				result = "error";
			}
			
			if ("success".equals(result)) {
				// 下载的Pci规划文件名称
				dlFileName = jobId + "_D2小区表.csv";
				// 下载的Pci规划文件全路径
				dlFileRealPath = dlFileRealdir + dlFileName;
				File realfile2 = new File(dlFileRealPath);
				if (!realfile2.exists()) {
					try {
						realfile2.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

			   try {
				   List<Map<String, Object>> d2CellRes = oracleMapper.getD2Cell(jobId);
					bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dlFileRealPath), "GBK"));
					// 标题头
					line = "小区标识," + "频点," + "干扰值";
					bw.write(line);
					bw.newLine();
					for (Map<String, Object> map : d2CellRes) {
						bw.write(map.get("CELL_ID") + "," + map.get("EARFCN") + "," + map.get("INTER_VAL"));
						bw.newLine();
					}
				} catch (IOException e) {
					logger.info("Pci规划计算的d2频数据不存在!");
					result = "error";
				}
				sourceFileList.add(d1Path);
				sourceFileList.add(dlFileRealPath);
			}
			if (bw != null) {
				try {
					bw.close();
				} catch (IOException e) {
				}
			}
		}
		// 下载的Pci规划文件名称
		dlFileName = pciTaskInfo.get(0).get("DL_FILE_NAME").toString();
		// 下载的Pci规划文件全路径
		dlFileRealPath = dlFileRealdir + jobId + "_PCI优化方案.xlsx";
		sourceFileList.add(dlFileRealPath);
		// System.out.println("sourceFileList:"+sourceFileList);
		File realdir = new File(dlFileRealdir);
		if (!realdir.exists()) {
			realdir.mkdirs();
		}
		File realfile = new File(dlFileRealPath);
		if (!realfile.exists()) {
			try {
				realfile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		List<Map<String, Object>> res = new ArrayList<Map<String, Object>>();
		Map<String, Object> one = null;
		String cellId = "";
		List<String> cells = null;
		try {
			List<Map<String, Object>> bestPlanRes = oracleMapper.getBestPlan(jobId);
				for (Map<String, Object> map : bestPlanRes) {
					one = new HashMap<String, Object>();
					cellId = map.get("CELL_ID").toString();
					cells = cellIdToCessInfo.get(cellId);
					if (cells == null) {
						logger.info("小区找不到对应工参数据：cellId ={}", cellId);
						one.put("cellName", "未知小区");
						one.put("oldPci", -1);
						one.put("oldEarfcn", -1);
					} else {
						// 小区名，pci,频点
						one.put("cellName", cells.get(0));
						one.put("oldPci", Integer.parseInt(cells.get(1)));
						one.put("oldEarfcn", Integer.parseInt(cells.get(2)));
					}
					one.put("cellId", cellId);
					int newEarfcn = Integer.parseInt(map.get("EARFCN").toString());
					if (newEarfcn == -1) {
						if (cells == null) {
							one.put("newEarfcn", "找不到对应工参数据");
						} else {
							one.put("newEarfcn", "找不到对应MR数据");
						}
					} else {
						one.put("newEarfcn", newEarfcn);
					}
					int newPci = Integer.parseInt(map.get("PCI").toString());
					if (newPci == -1) {
						if (cells == null) {
							one.put("newPci", "找不到对应工参数据");
						} else {
							one.put("newPci", "找不到对应MR数据");
						}
					} else {
						one.put("newPci", newPci);
					}
					one.put("oriInterVal", Double.parseDouble(map.get("OLD_INTER_VAL").toString()));
					one.put("interVal", Double.parseDouble(map.get("NEW_INTER_VAL").toString()));

					if (cellsMap.containsKey(cellId)) {
						one.put("remark", "修改小区");
					} else {
						one.put("remark", "MR其他小区");
					}
					res.add(one);
				}

				File outFile = null;
				String outFileName = dlFileName;
				String outFilePath = dlFileRealPath;
				FileTool.createExcelFile(dlFileRealPath, res);
				if (sourceFileList.size() >= 1) {
					outFileName = jobId + "_PCI优化.zip";
					outFilePath = ZipFileHandler.AddtoZip(sourceFileList, outFileName);
				}
				outFile = new File(outFilePath);
				if (outFile.exists()) {
					HttpHeaders headers = new HttpHeaders();
					String fileName = new String(outFileName.getBytes("UTF-8"), "iso-8859-1");// 为了解决中文名称乱码问题
					headers.setContentDispositionFormData("attachment", fileName);
					headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
					return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(outFile), headers,
							HttpStatus.CREATED);
				} else {
					logger.error("Pci规划结果文件不存在！,文件路径：{}", outFilePath);
					return new ResponseEntity<>(HttpStatus.NOT_FOUND);
				}
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("获取Pci规划结果源文件中，读取数据出错");
			result = "error";
		}
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);

	}

	/**
	 * 提交pci自动规划任务(采用日期数据)
	 */
	@RequestMapping("/submitNewPciPlanAnalysisTaskWithDayData")
	public ResponseEntity<String> submitNewPciPlanAnalysisTaskWithDayData(HttpServletRequest request) {
		String account = request.getParameter("account").toString();
		// 保存需要优化的小区列
		taskobj.getTaskInfo().setLteCells(request.getParameter("lteCells"));
		if (request.getParameter("filenames") != null && !"".equals(request.getParameter("filenames"))
				&& !"undefined".equals(request.getParameter("filenames").toLowerCase())) {
			taskobj.getTaskInfo().setSfFiles(request.getParameter("filenames"));
		} else {
			taskobj.getTaskInfo().setSfFiles("");
		}

		JobProfile pciPlanJob = new JobProfile();
		long pciPlanJobId = 0;
		String runMode = "";
		Map<String, Object> savePciPlanInfoResult = null;
		try {
			pciPlanJob.setAccount(account);
			pciPlanJob.setJobName(taskobj.getTaskInfo().getTaskName());
			pciPlanJob.setJobType("RNO_PCI_PLAN_NEW");
			pciPlanJob.setSubmitTime(new Date());
			pciPlanJob.setDescription(taskobj.getTaskInfo().getTaskDesc());
			pciPlanJob.setJobStateStr(JobParseStatus.Waiting.toString());
			// 在 rno_ms_job 表增加一条记录
			oracleMapper.addJob(pciPlanJob);

			pciPlanJobId = pciPlanJob.getJobId();
			tempJobId = pciPlanJobId;
			logger.debug("干扰矩阵任务jobId={}", pciPlanJobId);

			// 保存任务信息
			savePciPlanInfoResult = rnoPciAfpService.submitPciPlanAnalysisTask(pciPlanJobId, account, rnoThresholds,
					taskobj.getTaskInfo());

			if (Boolean.parseBoolean(savePciPlanInfoResult.get("flag").toString())) {

				// 如果运行模式是定时模式，文件上传成功后就直接结束
				runMode = rnoProperties.getRunMode();
				logger.debug("rnoProperties.getRunMode() = {}", runMode);

				if (runMode.equals("scheduler") || (runMode.equals("never"))) {
					logger.debug("运行模式是 scheduler 或 never 模式，直接结束。");
					return new ResponseEntity<>(HttpStatus.OK);
				} else {

					logger.debug("运行模式是 always 模式，直接执行任务。");

					new Thread() {
						public void run() {
							boolean res = false;
							// 从数据库中取得将运行的数据解析导入任务
							if (tempJobId > 0) {
								pciPlanJob.setJobId(tempJobId);
								pciPlanJob.setJobStateStr(JobParseStatus.Running.toString());
								oracleMapper.updateJobRunningStatus(pciPlanJob);

								// 更新任务开始时间
								pciPlanJob.setLaunchTime(new Date());
								oracleMapper.updateJobBegTime(pciPlanJob);

								res = rnoJobService.runJob(tempJobId);

								if (res) {
									pciPlanJob.setFinishTime(new Date());
									pciPlanJob.setJobStateStr(JobParseStatus.Succeded.toString());
									oracleMapper.updateJobEndTime(pciPlanJob);
								} else {
									pciPlanJob.setFinishTime(new Date());
									pciPlanJob.setJobStateStr(JobParseStatus.Fail.toString());
									oracleMapper.updateJobEndTime(pciPlanJob);
								}
							}
						}
					}.start();

				}
			}
		} catch (Exception e) {
			pciPlanJob.setFinishTime(new Date());
			pciPlanJob.setJobStateStr(JobParseStatus.Fail.toString());
			oracleMapper.updateJobEndTime(pciPlanJob);
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * 提交pci自动规划任务(采用矩阵文件)
	 */
	@RequestMapping("/submitNewPciPlanAnalysisTaskWithMatrix")
	public ResponseEntity<String> submitNewPciPlanAnalysisTaskWithMatrix(MultipartHttpServletRequest request) {

		logger.debug("进入submitNewPciPlanAnalysisTaskWithMatrix方法，ltecells={}", request.getParameter("lteCells"));

		String account = request.getParameter("account").toString();
		// 保存需要优化的小区列
		taskobj.getTaskInfo().setLteCells(request.getParameter("cells"));
		String cityId = request.getParameter("cityId");
		Date businessTime = new Date();

		JobProfile pciPlanJob = new JobProfile();
		long pciPlanJobId = 0;
		int MatrixFileUploadresult = 0;
		long jobId = 0;
		long dataRecId = 0;

		try {
			Iterator<String> itr = request.getFileNames();
			MultipartFile mpf;

			while (itr.hasNext()) {
				mpf = request.getFile(itr.next());
				logger.debug("cityId={}, businessTime={}, fileName={}", cityId, businessTime,
						mpf.getOriginalFilename());
				String fileName = mpf.getOriginalFilename();

				// 判断本地文件路径是否存在，否则创建
				File file = new File(multipartConfigElement.getLocation() + "/matix");
				if (!file.exists()) {
					file.mkdirs();
				}
				
				String nowName = UUID.randomUUID().toString().replaceAll("-", "");

				String filePath = Paths.get(multipartConfigElement.getLocation() + "/matix", nowName).toString();
				logger.debug("filePath:{}", filePath);
				File f = new File(filePath);
				// mpf.transferTo(f);
				BufferedReader br = null;
				BufferedWriter bw = null;
				String b = null;
				try {
					bw = new BufferedWriter(new FileWriter(f));
					br = new BufferedReader(new InputStreamReader(mpf.getInputStream(), "utf-8"));
					while ((b = br.readLine()) != null) {
						bw.write(b);// 输出字符串
						bw.newLine();// 换行
						bw.flush();
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						if (br != null) {
							br.close();
						}
						if (bw != null) {
							bw.close();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				logger.info("文件上传完成 :{}", fileName);

				JobProfile matrixJob = new JobProfile();

				matrixJob.setSubmitTime(new Date());
				matrixJob.setJobType("RNO_PCI_PLAN_MATRIX_NEW");
				matrixJob.setAccount(account);

				// 在 rno_ms_job 表增加一条记录
				oracleMapper.addJob(matrixJob);

				jobId = matrixJob.getJobId();
				logger.debug("干扰矩阵任务jobId={}", jobId);

				// 更新任务状态
				matrixJob.setJobStateStr(JobParseStatus.Waiting.toString());
				oracleMapper.updateJobRunningStatus(matrixJob);

				RnoDataCollectRec dataRec = new RnoDataCollectRec();

				dataRec.setAccount(account);
				dataRec.setBusinessDataType(24);
				dataRec.setBusinessTime(new Date());
				dataRec.setCityId(Long.parseLong(cityId));
				dataRec.setFileName(nowName);
				dataRec.setFileSize(mpf.getSize());
				dataRec.setFileStatus("等待解析");
				dataRec.setFullPath(filePath);
				dataRec.setOriFileName(fileName);
				dataRec.setUploadTime(new Date());
				dataRec.setJobId(jobId);

				MatrixFileUploadresult = oracleMapper.addStatus(dataRec);
				taskobj.getTaskInfo().setMatrixDataCollectId(dataRec.getDataCollectId());
				dataRecId = dataRec.getDataCollectId();
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("上传矩阵文件失败！");
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		try {
			pciPlanJob.setAccount(account);
			pciPlanJob.setJobName(taskobj.getTaskInfo().getTaskName());
			pciPlanJob.setJobType("RNO_PCI_PLAN_NEW");
			pciPlanJob.setSubmitTime(new Date());
			pciPlanJob.setDescription(taskobj.getTaskInfo().getTaskDesc());
			pciPlanJob.setJobStateStr(JobParseStatus.Waiting.toString());
			// 在 rno_ms_job 表增加一条记录
			oracleMapper.addJob(pciPlanJob);

			pciPlanJobId = pciPlanJob.getJobId();
			tempJobId = pciPlanJobId;

			Map<String, Object> map = new HashMap<String, Object>();
			map.put("dataId", dataRecId);
			map.put("jobId", pciPlanJobId);
			oracleMapper.updateJobIdWithDataId(map);

			logger.debug("干扰矩阵任务jobId={}", pciPlanJobId);

			// 保存任务信息
			Map<String, Object> savePciPlanInfoResult = rnoPciAfpService.submitPciPlanAnalysisTask(pciPlanJobId,
					account, rnoThresholds, taskobj.getTaskInfo());

			if (MatrixFileUploadresult > 0 && Boolean.parseBoolean(savePciPlanInfoResult.get("flag").toString())) {

				// 如果运行模式是定时模式，文件上传成功后就直接结束
				String runMode = rnoProperties.getRunMode();
				logger.debug("rnoProperties.getRunMode() = {}", runMode);

				if (runMode.equals("scheduler") || (runMode.equals("never"))) {
					logger.debug("运行模式是 scheduler 或 never 模式，直接结束。");
					return new ResponseEntity<>(HttpStatus.OK);
				} else {

					logger.debug("运行模式是 always 模式，直接执行任务。");

					new Thread() {
						public void run() {
							boolean res = false;
							// 从数据库中取得将运行的数据解析导入任务
							if (tempJobId > 0) {
								pciPlanJob.setJobId(tempJobId);
								pciPlanJob.setJobStateStr(JobParseStatus.Running.toString());
								oracleMapper.updateJobRunningStatus(pciPlanJob);

								// 更新任务开始时间
								pciPlanJob.setLaunchTime(new Date());
								oracleMapper.updateJobBegTime(pciPlanJob);

								res = rnoJobService.runJob(tempJobId);

								if (res) {
									pciPlanJob.setFinishTime(new Date());
									pciPlanJob.setJobStateStr(JobParseStatus.Succeded.toString());
									oracleMapper.updateJobEndTime(pciPlanJob);
								} else {
									pciPlanJob.setFinishTime(new Date());
									pciPlanJob.setJobStateStr(JobParseStatus.Fail.toString());
									oracleMapper.updateJobEndTime(pciPlanJob);
								}
							}
						}
					}.start();

				}
			}
		} catch (Exception e) {
			pciPlanJob.setFinishTime(new Date());
			pciPlanJob.setJobStateStr(JobParseStatus.Fail.toString());
			oracleMapper.updateJobEndTime(pciPlanJob);
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<>(HttpStatus.OK);
	}
	
}
