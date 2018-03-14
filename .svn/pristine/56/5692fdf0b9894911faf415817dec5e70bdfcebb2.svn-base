package com.iscreate.rnointerfermatrix.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.iscreate.rnointerfermatrix.mapper.oracle.OracleMapper;
import com.iscreate.rnointerfermatrix.mapper.spark.SparkMapper;
import com.iscreate.rnointerfermatrix.model.Area;
import com.iscreate.rnointerfermatrix.model.G4HoDescQueryCond;
import com.iscreate.rnointerfermatrix.model.G4MrDescQueryCond;
import com.iscreate.rnointerfermatrix.model.G4SfDescQueryCond;
import com.iscreate.rnointerfermatrix.model.JobParseStatus;
import com.iscreate.rnointerfermatrix.model.JobProfile;
import com.iscreate.rnointerfermatrix.model.Page;
import com.iscreate.rnointerfermatrix.model.RnoLteInterMatrixTaskInfo;
import com.iscreate.rnointerfermatrix.properties.RnoProperties;
import com.iscreate.rnointerfermatrix.service.RnoCommonService;
import com.iscreate.rnointerfermatrix.service.RnoInterferMatrixService;
import com.iscreate.rnointerfermatrix.task.JobExecutor;
import com.iscreate.rnointerfermatrix.tool.DateUtil;

@Controller
@SessionAttributes("userId")
public class RnoInterferMatrixController {

	private static final Logger logger = LoggerFactory.getLogger(RnoInterferMatrixController.class);
	
	private static Gson gson = new GsonBuilder().create();

	//private String account = "liu.yp@iscreate.com";

	private String allAreas;

	private List<Area> provincesList = new ArrayList<Area>();

	private List<Area> citiesList = new ArrayList<Area>();

	@Autowired
	@Qualifier("rnoCommonServiceImpl")
	private RnoCommonService rnoCommonService;
	
	@Autowired
	@Qualifier("rnoInterferMatrixServiceImpl")
	private RnoInterferMatrixService rnoInterferMatrixService;

	@Autowired
	private OracleMapper oracleMapper;
	
	@Autowired
	private SparkMapper sparkMapper;

	@Autowired
	private RnoProperties rnoProperties;

	@Autowired
	@Qualifier("lteInterferMatrixTask")
	private JobExecutor jobExecutor;
	
	/**
	 * 获取用户权限下的区域
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping("/")
	ModelAndView index(Map<String, Object> model, HttpServletRequest request, String account) {

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
		
		logger.debug("访问首页,account={}", account);
		
		Map<String, List<Area>> map;
		try {
			map = rnoCommonService.getAreaByAccount(account);
		} catch (Exception e) {
			model.put("error", "当前用户没有权限！");
			return new ModelAndView("rno_fail");
		}
		provincesList = map.get("provinceAreas");
		citiesList = map.get("cityAreas");
		model.put("provinces", provincesList);
		model.put("cities", citiesList);
		allAreas = gson.toJson(map.get("allAreas"));
		model.put("allAreas", allAreas);

		logger.debug("provinceAreas={}, city={}, allAreas={}", provincesList, citiesList, allAreas);
		return new ModelAndView("rno_4g_interfer_martix_manage_new");
	}

	/**
	 * 分页查询4g干扰矩阵信息
	 */
	@RequestMapping("/queryNewLteInterferMartixByPage")
	@ResponseBody
	Map<String, Object> queryNewLteInterferMartixByPage(HttpServletRequest request) {
		logger.debug("进入方法queryNewLteInterferMartixByPage.");

		Map<String, Object> result = new HashMap<String, Object>();

		Page newPage = new Page();
		newPage.setPageSize(Integer.parseInt(request.getParameter("hiddenPageSize")));
		newPage.setCurrentPage(Integer.parseInt(request.getParameter("hiddenCurrentPage")));
		newPage.setTotalCnt(Integer.parseInt(request.getParameter("hiddenTotalCnt")));
		newPage.setTotalPageCnt(Integer.parseInt(request.getParameter("hiddenTotalPageCnt")));

		Map<String, Object> cond = new HashMap<String, Object>();
		cond.put("cityId", Long.parseLong(request.getParameter("citymenu")));
		cond.put("interMartixType", request.getParameter("interMartixType"));
		cond.put("begTime", request.getParameter("begTime")+ " 00:00:00");
		cond.put("endTime", request.getParameter("latestAllowedTime") + " 23:59:59");
		

		List<Map<String, Object>> res = rnoInterferMatrixService.queryLteInterferMartixByPage(cond, newPage);

		logger.debug("计算以后，page=" + newPage);
		int totalCnt = newPage.getTotalCnt();
		newPage.setTotalPageCnt(totalCnt / newPage.getPageSize() + (totalCnt % newPage.getPageSize() == 0 ? 0 : 1));
		newPage.setForcedStartIndex(-1);
		result.put("page", newPage);
		result.put("data", res);
		
		return result;
	}

	/**
	 * 跳转新计算页面
	 */
	@RequestMapping("/initNewLteInterferMartixAdd")
	String initNewLteInterferMartixAdd(Map<String, Object> model, @ModelAttribute("userId") String account) {
		logger.debug("进入方法initNewLteInterferMartixAdd.");
		if(account==null||"".equals(account)){
			model.put("error", "非法登录用户，请选择正常渠道登录，谢谢！");
			return "rno_fail";
		}
		Map<String, List<Area>> map = rnoCommonService.getAreaByAccount(account);
		provincesList = map.get("provinceAreas");
		citiesList = map.get("cityAreas");
		model.put("provinces", provincesList);
		model.put("cities", citiesList);
		allAreas = gson.toJson(map.get("allAreas"));
		model.put("allAreas", allAreas);
		return "rno_4g_interfer_martix_add_new";
	}

	/**
	 * 检查这周是否计算过4g pci干扰矩阵
	 */
	@RequestMapping("isExistedNewLteInterMartixThisWeek")
	@ResponseBody
	Map<String, Object> isExistedNewLteInterMartixThisWeek(HttpServletRequest request) {
		logger.debug("进入方法isExistedNewLteInterMartixThisWeek.");

		Map<String, Object> result = new HashMap<String, Object>();

		long areaId = Long.parseLong(request.getParameter("cityId2").toString());
		Calendar cal = Calendar.getInstance();
		// 获取这个周一
		cal.add(Calendar.DATE, 0 * 7);
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		String thisMonday = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
		// 获取今天
		cal = Calendar.getInstance();
		String today = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
		// 查询结果
		List<Map<String, Object>> res = rnoInterferMatrixService.checkLteInterMartixThisWeek(areaId, thisMonday, today);
		// 转为星期命名
		String desc = "";
		boolean flag = false;
		if (res != null && res.size() > 0) {
			String createTime = res.get(0).get("CREATE_DATE").toString();
			String timeArray[] = createTime.split(" ");
			Date date = null;
			try {
				date = new SimpleDateFormat("yyyy-MM-dd").parse(createTime);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			cal = Calendar.getInstance();
			cal.setTime(date);
			int weekNum = cal.get(Calendar.DAY_OF_WEEK);

			switch (weekNum) {
			case 1:
				desc = "本周周日 " + timeArray[1] + " 曾经计算过干扰矩阵";
				flag = true;
				break;
			case 2:
				desc = "本周周一 " + timeArray[1] + " 曾经计算过干扰矩阵";
				flag = true;
				break;
			case 3:
				desc = "本周周二 " + timeArray[1] + " 曾经计算过干扰矩阵";
				flag = true;
				break;
			case 4:
				desc = "本周周三 " + timeArray[1] + " 曾经计算过干扰矩阵";
				flag = true;
				break;
			case 5:
				desc = "本周周四 " + timeArray[1] + " 曾经计算过干扰矩阵";
				flag = true;
				break;
			case 6:
				desc = "本周周五 " + timeArray[1] + " 曾经计算过干扰矩阵";
				flag = true;
				break;
			case 7:
				desc = "本周周六 " + timeArray[1] + " 曾经计算过干扰矩阵";
				flag = true;
				break;
			default:
				break;
			}
		}

		result.put("flag", flag);
		result.put("desc", desc);
		return result;
	}

	/**
	 * 通过城市ID检查任务名是否可用
	 */
	@RequestMapping("/checkTaskNameByCityId")
	@ResponseBody
	String checkTaskNameByCityId(HttpServletRequest request) {
		logger.debug("进入方法checkTaskNameByCityId.");
		String taskName = request.getParameter("taskName").toString();
		long cityId = Long.parseLong(request.getParameter("cityId").toString());
		List<String> taskNameList = rnoInterferMatrixService.queryTaskNameListByCityId(cityId);
		String result = "success";
		if (taskNameList.contains(taskName)) {
			result = "fail";
		}
		return result;
	}

	/**
	 * 
	 * @title 分页加载MR数据
	 */
	@RequestMapping("/queryMrDataByPage")
	@ResponseBody
	Map<String, Object> queryMrDataByPage(HttpServletRequest request) {
		logger.info("进入方法queryMrDataByPage.");

		Map<String, Object> result = new HashMap<String, Object>();

		Page newPage = new Page();
		newPage.setPageSize(Integer.parseInt(request.getParameter("hiddenPageSize")));
		newPage.setCurrentPage(Integer.parseInt(request.getParameter("hiddenCurrentPage")));
		newPage.setTotalCnt(Integer.parseInt(request.getParameter("hiddenTotalCnt")));
		newPage.setTotalPageCnt(Integer.parseInt(request.getParameter("hiddenTotalPageCnt")));

		long cityId = Long.parseLong(request.getParameter("cityId2").toString());
		String startTime = request.getParameter("begTime").toString();
		String endTime = request.getParameter("latestAllowedTime").toString();

		G4MrDescQueryCond g4MrDescQueryCond = new G4MrDescQueryCond();
		g4MrDescQueryCond.setCityId(cityId);
		g4MrDescQueryCond.setMeaBegTime(startTime);
		g4MrDescQueryCond.setMeaEndTime(endTime);
		List<Map<String, Object>> res = rnoInterferMatrixService.queryMrDataFromHbaseByPage(g4MrDescQueryCond, newPage);

		logger.debug("计算以后，page={},data={}",newPage,res);
		int totalCnt = newPage.getTotalCnt();
		newPage.setTotalPageCnt(totalCnt / newPage.getPageSize() + (totalCnt % newPage.getPageSize() == 0 ? 0 : 1));
		newPage.setForcedStartIndex(-1);

		result.put("page", newPage);
		result.put("data", res);

		return result;
	}

	/**
	 * 分页加载Ho数据
	 * 
	 */
	@RequestMapping("/queryHoDataByPage")
	@ResponseBody
	Map<String, Object> queryHoDataByPage(HttpServletRequest request) {
		logger.info("进入方法queryHoDataByPage.");

		Map<String, Object> result = new HashMap<String, Object>();

		Page newPage = new Page();
		newPage.setPageSize(Integer.parseInt(request.getParameter("hiddenPageSize")));
		newPage.setCurrentPage(Integer.parseInt(request.getParameter("hiddenCurrentPage")));
		newPage.setTotalCnt(Integer.parseInt(request.getParameter("hiddenTotalCnt")));
		newPage.setTotalPageCnt(Integer.parseInt(request.getParameter("hiddenTotalPageCnt")));

		long cityId = Long.parseLong(request.getParameter("cityId2").toString());
		String startTime = request.getParameter("begTime").toString();
		String endTime = request.getParameter("latestAllowedTime").toString();

		G4HoDescQueryCond g4HoDescQueryCond = new G4HoDescQueryCond();
		g4HoDescQueryCond.setCityId(cityId);
		g4HoDescQueryCond.setMeaBegTime(startTime);
		g4HoDescQueryCond.setMeaEndTime(endTime);
		List<Map<String, Object>> res = rnoInterferMatrixService.queryHoDataFromHbaseByPage(g4HoDescQueryCond, newPage);

		logger.debug("计算以后queryHoDataByPageForAjaxAction，page=" + newPage);
		int totalCnt = newPage.getTotalCnt();
		newPage.setTotalPageCnt(totalCnt / newPage.getPageSize() + (totalCnt % newPage.getPageSize() == 0 ? 0 : 1));
		newPage.setForcedStartIndex(-1);

		result.put("page", newPage);
		result.put("data", res);

		return result;
	}

	/**
	 * @title 分页加载sf数据
	 */
	@RequestMapping("/querySfFilesByPage")
	@ResponseBody
	Map<String, Object> querySfFilesByPage(HttpServletRequest request) {
		logger.debug("进入方法querySfFilesByPage.");

		Map<String, Object> result = new HashMap<String, Object>();

		long cityId = Long.parseLong(request.getParameter("cityId2").toString());
		String startTime = request.getParameter("begTime").toString();
		String endTime = request.getParameter("latestAllowedTime").toString();

		G4SfDescQueryCond g4SfDescQueryCond = new G4SfDescQueryCond();
		g4SfDescQueryCond.setCityId(cityId);
		g4SfDescQueryCond.setMeaBegTime(startTime);
		g4SfDescQueryCond.setMeaEndTime(endTime);
		g4SfDescQueryCond.setFactory("ALL");

		Page newPage = new Page();
		newPage.setPageSize(Integer.parseInt(request.getParameter("hiddenPageSize")));
		newPage.setCurrentPage(Integer.parseInt(request.getParameter("hiddenCurrentPage")));
		newPage.setTotalCnt(Integer.parseInt(request.getParameter("hiddenTotalCnt")));
		newPage.setTotalPageCnt(Integer.parseInt(request.getParameter("hiddenTotalPageCnt")));

		List<Map<String, Object>> res = rnoInterferMatrixService.querySfDataFromHbaseByPage(g4SfDescQueryCond, newPage);

		logger.debug("计算以后，page=" + newPage);
		int totalCnt = newPage.getTotalCnt();
		newPage.setTotalPageCnt(totalCnt / newPage.getPageSize() + (totalCnt % newPage.getPageSize() == 0 ? 0 : 1));
		newPage.setForcedStartIndex(-1);

		result.put("page", newPage);
		result.put("data", res);

		return result;
	}

	/**
	 * 
	 * @title 新增4g pci 干扰矩阵
	 */
	@RequestMapping("/addNewLteInterMartix")
	@ResponseBody
	Map<String, Object> addNewLteInterMartix(HttpServletRequest request, @ModelAttribute("userId")String account) {
		logger.debug("进入方法addNewLteInterMartix。begTime={}, endTime={}, sfFileCounts={}",
				request.getParameter("begTime"), request.getParameter("latestAllowedTime"),
				request.getParameter("sffilecounts"));
		account = request.getParameter("account").toString();
		String lastMonday, lastSunday;
		
		RnoLteInterMatrixTaskInfo taskInfo = new RnoLteInterMatrixTaskInfo();
		taskInfo.setAccount(account);
		taskInfo.setJobType(request.getParameter("jobType"));
		taskInfo.setBegTime(request.getParameter("begTime"));
		taskInfo.setEndTime(request.getParameter("latestAllowedTime"));
		taskInfo.setDataType(request.getParameter("dataType"));
		taskInfo.setCityId(Integer.parseInt(request.getParameter("cityId2")));
		taskInfo.setTaskName(request.getParameter("taskName"));
		taskInfo.setSameFreqCellCoefWeight(Double.parseDouble(request.getParameter("SAMEFREQCELLCOEFWEIGHT")));
		taskInfo.setSwitchRatioWeight(Double.parseDouble(request.getParameter("SWITCHRATIOWEIGHT")));
		taskInfo.setSfFiles(request.getParameter("sffiles"));
		if (request.getParameter("sffilecounts").toString() == "undefined" || request.getParameter("sffilecounts").toString() == ""
				|| request.getParameter("sffilecounts").toString() == null || request.getParameter("sffilecounts").toString().hashCode() == 0

) {
			taskInfo.setSfFileCounts(0l);
		} else {
			taskInfo.setSfFileCounts(Long.parseLong(request.getParameter("sffilecounts").toString()));
		}

		Calendar cal = Calendar.getInstance();
		// 获取上周周一
		cal.add(Calendar.DATE, -1 * 7);
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		lastMonday = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
		// 获取上周周日
		cal.add(Calendar.DATE, 1 * 7);
		cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		lastSunday = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());

		DateUtil dateUtil = new DateUtil();
		Date startTime = dateUtil.parseDateArbitrary(taskInfo.getBegTime());
		Date endTime = dateUtil.parseDateArbitrary(taskInfo.getEndTime());
		Date lastMon = dateUtil.parseDateArbitrary(lastMonday);
		Date lastSun = dateUtil.parseDateArbitrary(lastSunday);

		boolean isDateRight = false;
		boolean isMrExist = false;
		boolean isHoExist = false;
		// 判断日期是否符合要求
		if ((endTime.after(startTime) || endTime.equals(startTime))
				&& (lastSun.after(endTime) || lastSun.equals(endTime))
				&& (lastSun.after(startTime) || lastSun.equals(startTime))
				&& (startTime.after(lastMon) || startTime.equals(lastMon))
				&& (endTime.after(lastMon) || endTime.equals(lastMon))) {

			isDateRight = true;
		}
		// 判断日期范围是否存在MR数据
		int mrCnt = rnoInterferMatrixService.queryMrDataCountByCond(taskInfo);
		int hoCnt = rnoInterferMatrixService.queryHoDataCountByCond(taskInfo);
		if (mrCnt > 0) {
			isMrExist = true;
		}
		if (hoCnt > 0) {
			isHoExist = true;
		}

		isDateRight = true;
		Map<String, Object> result = new HashMap<String, Object>();
		String dataType = taskInfo.getDataType();
		boolean submitTaskResult = false;
	
			if (isMrExist || isHoExist) {
				// 提交干扰矩阵计算任务
				submitTaskResult = rnoInterferMatrixService.addLteInterMartix(taskInfo);
			}

		if (submitTaskResult) {
			new Thread() {
				public void run() {
					JobProfile job = new JobProfile();
					String jobId = null;
					boolean res = false;

					String runMode = rnoProperties.getRunMode();
					if (runMode.equals("scheduler") || (runMode.equals("never"))) {
						logger.debug("运行模式是 scheduler 或 never 模式，直接结束。");
					} else {

						try {
							job.setJobStateStr(JobParseStatus.Waiting.toString());
							job.setJobType("CALC_LTE_INTERFER_MATRIX_NEW");
							jobId = oracleMapper.getJoBId(job);
							logger.debug("运行模式是 always 模式，直接执行任务。");

							// 从数据库中取得将运行的数据解析导入任务
							if (jobId != null) {
								job.setJobId(Long.parseLong(jobId));
								job.setJobStateStr(JobParseStatus.Running.toString());
								oracleMapper.updateJobRunningStatus(job);

								// 更新任务开始时间
								job.setLaunchTime(new Date());
								oracleMapper.updateJobBegTime(job);

								res = jobExecutor.runJobInternal(Long.parseLong(jobId));

								if (res) {
									job.setFinishTime(new Date());
									job.setJobStateStr(JobParseStatus.Succeded.toString());
									oracleMapper.updateJobEndTime(job);
								} else {
									job.setFinishTime(new Date());
									job.setJobStateStr(JobParseStatus.Fail.toString());
									oracleMapper.updateJobEndTime(job);
								}
							}
						} catch (Exception e) {
							job.setFinishTime(new Date());
							job.setJobStateStr(JobParseStatus.Fail.toString());
							oracleMapper.updateJobEndTime(job);
							e.printStackTrace();
						}
					}

				};
			}.start();
		}
		result.put("isMrExist", isMrExist);
		result.put("isHoExist", isHoExist);
		result.put("isDateRight", isDateRight);
		result.put("dataType", dataType);
		result.put("result", submitTaskResult);
		return result;
	}

	/**
	 * 查询指定job的报告
	 * 
	 */
	@RequestMapping("/queryJobReport")
	@ResponseBody
	Map<String, Object> queryJobReport(HttpServletRequest request) {
		logger.debug("queryJobReport.jobId={}", request.getParameter("jobId"));

		Page newPage = new Page();
		Map<String, Object> result = new HashMap<>();
		if (StringUtils.isBlank(request.getParameter("jobId"))) {
			logger.error("未传入一个有效的jobid！无法查看其报告！");
			newPage.setTotalCnt(0);
			newPage.setTotalPageCnt(0);
			result.put("page", newPage);
			result.put("data", "");
		    logger.debug("退出queryJobReportAjaxAction。输出：{}", result);
			return result;
		}

		newPage.setCurrentPage(Integer.parseInt(request.getParameter("hiddenCurrentPage")));
		newPage.setPageSize(Integer.parseInt(request.getParameter("hiddenPageSize")));
		newPage.setTotalCnt(Integer.parseInt(request.getParameter("hiddenTotalCnt")));
		newPage.setTotalPageCnt(Integer.parseInt(request.getParameter("hiddenTotalPageCnt")));

		Long jobId = Long.parseLong(request.getParameter("jobId"));
		int cnt = 0;
		if (newPage.getTotalCnt() < 0) {
			cnt = rnoCommonService.queryJobReportCnt(jobId);
			newPage.setTotalCnt(cnt);
		}

		Map<String, Object> map = new HashMap<String, Object>();
		int start = (newPage.getPageSize() * (newPage.getCurrentPage() - 1) + 1);
		int end = (newPage.getPageSize() * newPage.getCurrentPage());
		map.put("jobId", jobId);
		map.put("start", start);
		map.put("end", end);

		List<Map<String, Object>> reportRecs = rnoCommonService.queryJobReportByPage(map);
		logger.debug("reportRecs size:{}", reportRecs == null ? 0 : reportRecs.size());
		//String result1 = gson.toJson(reportRecs);

		int totalCnt = newPage.getTotalCnt();
		newPage.setTotalPageCnt(totalCnt / newPage.getPageSize() + (totalCnt % newPage.getPageSize() == 0 ? 0 : 1));
		newPage.setForcedStartIndex(-1);

		result.put("page", newPage);
		result.put("data", reportRecs);
		return result;
	}

	/**
	 * @title 下载4g矩阵计算任务结果文件
	 */
	@RequestMapping("/downloadNewLteInterMatrixFile")
	@ResponseBody
	ResponseEntity<byte[]> downloadNewLteInterMatrixFile(HttpServletRequest request) {
		int jobId = Integer.parseInt(request.getParameter("jobId"));
		logger.debug("进入方法downloadNewLteInterMatrixFile。 下载4g矩阵计算结果文件， jobId=" + jobId);

		String error = "";
		// 下载的4g矩阵计算文件名称
		String dlFileName = jobId + "_4G矩阵结果表.csv";
		// 下载的4g矩阵计算文件全路径
		String path = rnoProperties.getDownloadFilePath() + "/";
		String dlFileRealPath = path + dlFileName;
		File dir = new File(path);
		if(!dir.exists()){
		  dir.mkdirs();
		}
		
		File file = new File(dlFileRealPath);
		
		// 标题头
		String line = "小区标识," + "邻区标识," + "关联度," + "小区PCI," + "邻区PCI," + "小区频点," + "邻区频点," + "MR关联度," + "HO关联度,"
				+ "扫频关联度";

		BufferedWriter bw = null;
		FileWriter fw = null;

		try {
			//获取干扰矩阵数据
			List<Map<String, Object>> data = sparkMapper.downloadMatrix(jobId);
			
			if (!file.exists()) {
			    file.createNewFile();
			   }

			fw = new FileWriter(file.getAbsoluteFile());
			bw = new BufferedWriter(fw);
			// 写入标题头
			bw.write(line);
			bw.newLine();
			
			for(Map<String, Object> map : data){
				bw.write(map.get("cell").toString()+","+map.get("ncell").toString()
						+","+map.get("rela_val").toString()+","+map.get("cell_pci").toString()
						+","+map.get("ncell_pci").toString()+","+map.get("cell_earfcn").toString()
						+","+map.get("ncell_earfcn").toString()+","+map.get("mr_rela").toString()
						+","+map.get("ho_rela").toString()+","+map.get("sf_rela").toString());
				bw.newLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
			error = "获取4g矩阵计算结果源文件中，读取数据出错";
			logger.info(error);
			return new ResponseEntity<>(HttpStatus.FAILED_DEPENDENCY);
		} finally {
			try {
				bw.flush();
				fw.flush();
				if (bw != null) {
					bw.close();
				}
				if (fw != null) {
					bw.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
				return new ResponseEntity<>(HttpStatus.FAILED_DEPENDENCY);
			}
		}

		try {
			HttpHeaders headers = new HttpHeaders();
			try {
				String fileName = new String(dlFileName.getBytes("UTF-8"), "iso-8859-1");// 为了解决中文名称乱码问题
				headers.setContentDispositionFormData("attachment", fileName);
				headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
			} catch (UnsupportedEncodingException e1) {
				error = "设置文件名失败";
				logger.error(error);
				return new ResponseEntity<>(HttpStatus.FAILED_DEPENDENCY);
			}
			return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file), headers, HttpStatus.CREATED);
		} catch (IOException e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.FAILED_DEPENDENCY);
		}

	}
	
}
