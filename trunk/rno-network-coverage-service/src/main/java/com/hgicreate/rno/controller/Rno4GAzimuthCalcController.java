package com.hgicreate.rno.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hgicreate.rno.mapper.G4AzimuthCalcMapper;
import com.hgicreate.rno.model.*;
import com.hgicreate.rno.properties.RnoProperties;
import com.hgicreate.rno.service.Rno4GAzimuthCalcService;
import com.hgicreate.rno.service.RnoCommonService;
import com.hgicreate.rno.service.task.pci.Rno4GAzimuthCalcTask;
import com.hgicreate.rno.tool.DateUtil;
import com.hgicreate.rno.tool.FileTool;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.*;
@CrossOrigin
@RestController
public class Rno4GAzimuthCalcController {

	private static final Logger logger = LoggerFactory.getLogger(Rno4GAzimuthCalcController.class);

	private static Gson gson = new GsonBuilder().create();

	//private String account = "liu.yp@iscreate.com";
	
	private String error;

	@Autowired
	@Qualifier("rnoCommonServiceImpl")
	private RnoCommonService rnoCommonService;

	@Autowired
	@Qualifier("rno4GAzimuthCalcServiceImpl")
	private Rno4GAzimuthCalcService rno4GAzimuthCalcService;

	@Autowired
	private G4AzimuthCalcMapper g4AzimuthCalcMapper;

	@Autowired
	private RnoProperties rnoProperties;

	@Autowired
	private Rno4GAzimuthCalcTask rno4GAzimuthCalcTask;

	private List<Area> provincesList = new ArrayList<Area>();

	private List<Area> citiesList = new ArrayList<Area>();

	private String allAreas;

	private RnoLteInterferCalcTask taskobj = new RnoLteInterferCalcTask();

	@Autowired
	private FileTool fileTool;
	/**
	 * 获取用户权限下的区域
	 * 
	 * @param account
	 * @return
	 */
	@RequestMapping("/getAreas/{account:.+}")
	Map<String, Object> index(@PathVariable("account") String account) {
		logger.debug("访问首页");
		Map<String, Object> model = new HashMap<String,Object>();
		Map<String, List<Area>> map = rnoCommonService.getAreaByAccount(account);
		provincesList = map.get("provinceAreas");
		citiesList = map.get("cityAreas");
		model.put("provinces", provincesList);
		model.put("cities", citiesList);
		allAreas = gson.toJson(map.get("allAreas"));
		model.put("allAreas", allAreas);
		logger.debug("provinceAreas={}, city={}, allAreas={}", provincesList, citiesList, allAreas);
		return model;
	}
	/**
	 * 根据父区域ID，获取指定类型的子区域列表
	 */
	@RequestMapping("/getSubAreaByParentAreaForAjaxAction/{parentAreaId}/{subAreaLevel}/{account:.+}")
	List<Area> getSubAreaByParentAreaForAjaxAction(@PathVariable long parentAreaId,@PathVariable String subAreaLevel,@PathVariable String account) {
		logger.debug("进入getSubAreaByParentAreaForAjaxAction方法,parentAreaId={},subAreaLevel={}",parentAreaId,subAreaLevel);
		if(subAreaLevel.equals("city")){
			subAreaLevel= "市";
		}else if(subAreaLevel.equals("country")){
			subAreaLevel= "区/县";
		}
		List<Area> areas = rnoCommonService
				.getSpecialSubAreasByAccountAndParentArea(account,
						parentAreaId, subAreaLevel);
		logger.info("退出getSubAreaByParentAreaForAjaxAction。输出：" + areas);
		return areas;
	}

	@RequestMapping(value = "/query4GAzimuthCalcTaskByPageForAjaxAction", method = RequestMethod.POST)
	public String query4GAzimuthCalcTaskByPageForAjaxAction(
			String cityId,  String endSubmitTime,
			 String meaTime,  String startSubmitTime,
			 String taskName,  String taskStatus,
			 String currentPage, String pageSize, String provinceId2, String account,String isMine) {
		logger.info("进入：query4GAzimuthCalcTaskByPageForAjaxAction。cond={},{},{},{},{},{},{},page={},{},account={},isMine={}",cityId,startSubmitTime,endSubmitTime,meaTime,taskName,taskStatus,provinceId2,currentPage,pageSize,account,isMine);
		Page newPage = new Page();
		newPage.setPageSize(Integer.parseInt(pageSize));
		newPage.setCurrentPage(Integer.parseInt(currentPage));
		Map<String, String> cond = new HashMap<String, String>();
		cond.put("cityId", cityId);
		cond.put("taskName", taskName);
		cond.put("taskStatus", taskStatus);
		cond.put("meaTime", meaTime);
		cond.put("startSubmitTime", startSubmitTime);
		cond.put("endSubmitTime", endSubmitTime);
		cond.put("isMine", isMine);
		List<Map<String, Object>> pciTasks = rno4GAzimuthCalcService.query4GAzimuthCalcTaskByPage(cond, newPage, account);
		String res = gson.toJson(pciTasks);

		int totalCnt = newPage.getTotalCnt();
		newPage.setTotalPageCnt(totalCnt / newPage.getPageSize()
				+ (totalCnt % newPage.getPageSize() == 0 ? 0 : 1));
		newPage.setForcedStartIndex(-1);

		String pstr = gson.toJson(newPage);
		String result = "{'page':" + pstr + ",'data':" + res + "}";
		logger.info("退出query4GAzimuthCalcTaskByPageForAjaxAction。输出：" + result);
		return result;
//		writer.write(result);
	}
	/**
	 * 终止Pci规划任务
	 * 
	 */
	/*@RequestMapping("/stop4GAzimuthJobByJobIdForAjaxAction")
	@ResponseBody
	void stop4GAzimuthJobByJobIdForAjaxAction(HttpServletRequest request, PrintWriter writer) {
		logger.debug("进入方法：stop4GAzimuthJobByJobIdForAjaxAction。jobId={}, mrJobId={}",
				Long.parseLong(request.getParameter("jobId")), request.getParameter("mrJobId"));
		MrJobCond mrJobCond = new MrJobCond(Long.parseLong(request.getParameter("jobId")),
				request.getParameter("mrJobId"), account, "");
		Map<String, Object> res = new HashMap<String, Object>();
		boolean flag = rno4GAzimuthCalcService.stopJobByJobIdAndMrJobId(mrJobCond);
		res.put("flag", flag);
		String result = gson.toJson(res);
		writer.write(result);
	}*/

	/**
	 * 查询指定job的报告
	 * 
	 */
	@RequestMapping("/queryJobReportAjaxAction/{jobId}")
	public String queryJobReportAjaxAction(@PathVariable("jobId") long jobId) {
		logger.debug("queryJobReportAjaxAction.jobId={}", jobId);

		Page newPage = new Page();

//		Long jobId = Long.parseLong(request.getParameter("attachParams['jobId']"));
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

		List<Report> reportRecs = rnoCommonService.queryJobReportByPage(map);
		logger.debug("reportRecs size:{}", reportRecs == null ? 0 : reportRecs.size());
		String result1 = gson.toJson(reportRecs);

		int totalCnt = newPage.getTotalCnt();
		newPage.setTotalPageCnt(totalCnt / newPage.getPageSize() + (totalCnt % newPage.getPageSize() == 0 ? 0 : 1));
		newPage.setForcedStartIndex(-1);

		String pstr = gson.toJson(newPage);
		String result = "{'page':" + pstr + ",'data':" + result1 + "}";
		return result;
	}

	/**
	 * 提交4g方位角计算即网络覆盖分析任务(采用日期数据)
	 */
	@RequestMapping("/submit4GAzimuthCalcTaskAction")
	ResponseEntity<String> submit4GAzimuthCalcTaskAction(HttpServletRequest request,
			 String cityId,  String endTime,
			 String startTime,  String taskDesc,
			 String taskName, String account) {
		logger.debug("后中方法 submit4GAzimuthCalcTaskAction cityID={},endTime={},startTime={},taskDesc={},taskName={},account={}",cityId,endTime,startTime,taskDesc,taskName,account);
		// 保存需要优化的小区列
//		taskobj.getTaskInfo().setLteCells(request.getParameter("lteCells"));

		JobProfile job = new JobProfile();
		boolean res = false;
		long jobId = 0;
		String runMode = "";
		Map<String, Object> saveInfoResult = null;
		Map<String, Object> taskInfo = new HashMap<String, Object>();
		try {
			taskInfo.put("CITYID", cityId);
			taskInfo.put("ENDTIME", endTime);
			taskInfo.put("STARTTIME", startTime);
			taskInfo.put("TASKDESC", taskDesc);
			taskInfo.put("TASKNAME", taskName);
			// 保存任务信息
			saveInfoResult = rno4GAzimuthCalcService.submit4GAzimuthCalcTask(account, taskInfo);

			if (Boolean.parseBoolean(saveInfoResult.get("flag").toString())) {

				// 如果运行模式是定时模式，文件上传成功后就直接结束
				runMode = rnoProperties.getRunMode();
				logger.debug("rnoProperties.getRunMode() = {}", runMode);

				if (runMode.equals("scheduler") || (runMode.equals("never"))) {
					logger.debug("运行模式是 scheduler 或 never 模式，直接结束。");
					return new ResponseEntity<>(HttpStatus.OK);
				} else {

					logger.debug("运行模式是 always 模式，直接执行任务。");
					jobId = Long.parseLong(saveInfoResult.get("jobId").toString());
					if (jobId > 0) {
						job.setJobId(jobId);
						job.setJobStateStr(JobParseStatus.Running.toString());
						g4AzimuthCalcMapper.updateJobRunningStatus(job);

						// 更新任务开始时间
						job.setLaunchTime(new Date());
						g4AzimuthCalcMapper.updateJobBegTime(job);

						res = rno4GAzimuthCalcTask.runJobInternal(jobId);

						if (res) {
							job.setFinishTime(new Date());
							job.setJobStateStr(JobParseStatus.Succeded.toString());
							g4AzimuthCalcMapper.updateJobEndTime(job);
						} else {
							job.setFinishTime(new Date());
							job.setJobStateStr(JobParseStatus.Fail.toString());
							g4AzimuthCalcMapper.updateJobEndTime(job);
						}
					}
				}
			}
		} catch (Exception e) {
			job.setFinishTime(new Date());
			job.setJobStateStr(JobParseStatus.Fail.toString());
			g4AzimuthCalcMapper.updateJobEndTime(job);
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@RequestMapping(value="/download4GAzimuthFileAction/{jobId}")
	ResponseEntity<byte[]> download4GAzimuthFileAction(@PathVariable("jobId") long jobId) {
		logger.info("后台方法  download4GAzimuthFileAction 下载4g方位角计算结果文件， jobId=" + jobId
				);
		
		List<Map<String,Object>> pciTaksInfo = rno4GAzimuthCalcService.query4GAzimuthJobRecByJobId(jobId);
		if(pciTaksInfo.size() <= 0) {
			error="不存在该"+jobId+"任务信息";
//			return "fail";
			logger.error(error);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		//区域ID
		long cityId = Long.parseLong(pciTaksInfo.get(0).get("CITY_ID").toString());

		//下载的4g方位角计算文件名称
		String dlFileName = pciTaksInfo.get(0).get("DL_FILE_NAME").toString();
		//下载的4g方位角计算文件全路径
		DateUtil dateUtil = new DateUtil();
		Calendar calendar = new GregorianCalendar();
		Date createDate = dateUtil.parseDateArbitrary(
				pciTaksInfo.get(0).get("CREATE_TIME").toString());
		calendar.setTime(createDate);
		String path = rnoProperties.getDownloadFilePath();
		String dlFileRealdir = path+"/"+calendar.get(Calendar.YEAR)+"/"
				+ (calendar.get(Calendar.MONTH) + 1) + "/";
		String dlFileRealPath = dlFileRealdir + dlFileName;
		File realdir=new File(dlFileRealdir);
		if(!realdir.exists()){
			realdir.mkdirs();
		}
		File realfile=new File(dlFileRealPath);
		if(!realfile.exists()){
			try {
				realfile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		List<Map<String,Object>> res = new ArrayList<Map<String,Object>>();
		Map<String,Object> one = null;

		String result = "success";
		String line="";
		try {
			res = rno4GAzimuthCalcService.queryNetWorkCoverResByJobId(jobId);
		}catch(Exception e) {
			e.printStackTrace();
			logger.info("获取4g方位角计算结果源文件中，读取数据出错");
			error="获取4g方位角计算结果源文件中，读取数据出错";
			result = "error"; 
		}finally{

			//可以生成excel结果文件
			try {
				File outFile = new File(dlFileRealPath);
				if (!outFile.exists()) {
					logger.error("4g方位角计算结果文件不存在！");
					error="4g方位角计算结果文件不存在！,文件路径："+dlFileRealPath;
//					return "fail";
					logger.error(error);
					return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
				} 
				boolean flag = fileTool.create4GAzimuthExcelFile(dlFileRealPath,res);
				if(!flag){
					logger.error("4g方位角计算结果写入excel文件失败！");
					error="4g方位角计算结果写入excel文件失败！,文件路径："+dlFileRealPath;
//					return "fail";
					logger.error(error);
					return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
				}
				if (outFile.exists()) {
					HttpHeaders headers = new HttpHeaders();
					String fileName = new String(dlFileName.getBytes("UTF-8"), "iso-8859-1");// 为了解决中文名称乱码问题
					headers.setContentDispositionFormData("attachment", fileName);
					headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
					return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(outFile), headers,
							HttpStatus.CREATED);
				} else {
					logger.error("Pci规划结果文件不存在！,文件路径：{}", dlFileRealPath);
					return new ResponseEntity<>(HttpStatus.NOT_FOUND);
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
		}
		if(result.equals("error")){
			logger.error(error);
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//			return "fail";
		}
//		return result;
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}
}
