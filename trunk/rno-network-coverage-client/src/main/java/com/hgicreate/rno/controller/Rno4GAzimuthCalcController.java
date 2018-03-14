package com.hgicreate.rno.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@Component("restClient")
public class Rno4GAzimuthCalcController {

	private static final Logger logger = LoggerFactory.getLogger(Rno4GAzimuthCalcController.class);

	private static Gson gson = new GsonBuilder().create();

	private String error;
	private String allAreas;

//	private String account = "";
    @Value("${service-url}")
    private String ServiceUrl;
	@Autowired
	private RestTemplate restTemplate;
	/**
	 * 获取用户权限下的区域
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/")
    ModelAndView index(Map<String, Object> model,HttpServletRequest request,String account) {

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
		logger.debug("访问首页,account={}",account);
        String res = restTemplate.getForObject(ServiceUrl + "getAreas/{account}", String.class, account);
        JSONObject obj = JSONObject.parseObject(res);
        model.put("provinces", obj.get("provinces"));
        model.put("cities", obj.get("cities"));
        model.put("allAreas", obj.get("allAreas"));
        logger.debug("index.res={}", res);
        return new ModelAndView("rno_4g_azimuth_calc");
    }
	/**
	 * 根据父区域ID，获取指定类型的子区域列表
	 */
	@RequestMapping(value = "/getSubAreaByParentAreaForAjaxAction", method = RequestMethod.POST)
	String getSubAreaByParentAreaForAjaxAction(HttpServletRequest request,@RequestParam("parentAreaId") long parentAreaId,
											   @RequestParam("subAreaLevel") String subAreaLevel) {
		logger.debug("进入getSubAreaByParentAreaForAjaxAction方法,parentAreaId={},subAreaLevel={}",parentAreaId,subAreaLevel);
		String account = request.getSession().getAttribute("account").toString();
		if(subAreaLevel.equals("市")){
			subAreaLevel= "city";
		}else if(subAreaLevel.equals("区/县")){
			subAreaLevel= "country";
		}
		String result = restTemplate.getForObject(ServiceUrl+"getSubAreaByParentAreaForAjaxAction/{parentAreaId}/{subAreaLevel}/{account}",String.class,parentAreaId,subAreaLevel,account);
		logger.debug("getSubAreaByParentAreaForAjaxAction  返回result={}"+result);
		return result;
	}
	
	/*
	 * 查询4g方位角计算任务
	 */
	@ResponseBody
	@RequestMapping(value = "/query4GAzimuthCalcTaskByPageForAjaxAction", method = RequestMethod.POST)
	 String query4GAzimuthCalcTaskByPageForAjaxAction(HttpServletRequest request,
			@RequestParam(value="cond['cityId']",required=false) String cityId,@RequestParam(value="cond['endSubmitTime']",required=false) String endSubmitTime,
			@RequestParam(value="cond['meaTime']",required=false) String meaTime, @RequestParam("cond['startSubmitTime']") String startSubmitTime,
			@RequestParam("cond['taskName']") String taskName, @RequestParam("cond['taskStatus']") String taskStatus,
			@RequestParam("page.currentPage") String currentPage,@RequestParam("page.pageSize") String pageSize,@RequestParam("provinceId2") String provinceId2) {
		logger.info("进入：query4GAzimuthCalcTaskByPageForAjaxAction。cond={},{},{},{},{},{},page={},{},{}",cityId,startSubmitTime,endSubmitTime,meaTime,taskName,taskStatus,currentPage,pageSize,provinceId2);
		String account = request.getSession().getAttribute("account").toString();
		String isMine = request.getParameter("cond['isMine']");
		MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
		params.add("cityId",cityId);
		params.add("endSubmitTime",endSubmitTime);
		params.add("meaTime",meaTime);
		params.add("startSubmitTime",startSubmitTime);
		params.add("taskName",taskName);
		params.add("taskStatus",taskStatus);
		params.add("currentPage",currentPage);
		params.add("pageSize",pageSize);
		params.add("provinceId2",provinceId2);
		params.add("isMine",isMine);
		params.add("account",account);
		String result = restTemplate.postForObject(ServiceUrl+"query4GAzimuthCalcTaskByPageForAjaxAction",params,String.class);
		logger.info("退出query4GAzimuthCalcTaskByPageForAjaxAction。输出：" + result);
		return result;
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
	@RequestMapping(value = "/queryJobReportAjaxAction", method = RequestMethod.POST)
	public String queryJobReportAjaxAction(@RequestParam("attachParams['jobId']") long jobId) {
		logger.debug("queryJobReportAjaxAction.jobId={}", jobId);

		String result = restTemplate.getForObject(ServiceUrl+"queryJobReportAjaxAction/{jobId}",String.class,jobId);
		return result;
	}

	/**
	 * 提交4g方位角计算即网络覆盖分析任务(采用日期数据)
	 */
	@RequestMapping(value = "/submit4GAzimuthCalcTaskAction", method = RequestMethod.POST,produces={"application/json;charset=UTF-8"})
	ResponseEntity<String> submit4GAzimuthCalcTaskAction(HttpServletRequest request,
			@RequestParam("taskInfo.CITYID") String cityId, @RequestParam("taskInfo.ENDTIME") String endTime,
			@RequestParam("taskInfo.STARTTIME") String startTime, @RequestParam("taskInfo.TASKDESC") String taskDesc,
			@RequestParam("taskInfo.TASKNAME") String taskName) {

		// 保存需要优化的小区列
//		taskobj.getTaskInfo().setLteCells(request.getParameter("lteCells"));
		String account = request.getSession().getAttribute("account").toString();
		MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
		params.add("cityId",cityId);
		params.add("endTime",endTime);
		params.add("startTime",startTime);
		params.add("taskDesc",taskDesc);
		params.add("taskName",taskName);
		params.add("account",account);
		ResponseEntity<String> result = restTemplate.postForObject(ServiceUrl+"submit4GAzimuthCalcTaskAction",params,ResponseEntity.class);
		return result;
	}


	@RequestMapping(value = "/download4GAzimuthFileAction", method = RequestMethod.POST)
	@ResponseBody
	ResponseEntity download4GAzimuthFileAction(@RequestParam("jobId") long jobId) {
		logger.info("前台调用 download4GAzimuthFileAction 下载4g方位角计算结果文件， jobId=" + jobId
				);
		ResponseEntity<byte[]> result = restTemplate.getForEntity(ServiceUrl+"download4GAzimuthFileAction/{jobId}",byte[].class,jobId);
		return result;
	}
}
