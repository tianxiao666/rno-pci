package com.hgicreate.rno.controller;

import com.alibaba.fastjson.JSONObject;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

@RestController
@Component("restClient")
public class RnoLteDynaOverGraphController {

	private static final Logger logger = LoggerFactory.getLogger(RnoLteDynaOverGraphController.class);

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
	ModelAndView index(Map<String, Object> model, HttpServletRequest request,String account) {

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
		model.put("countryAreas", obj.get("countryAreas"));
		model.put("preFiveDayTime", obj.get("preFiveDayTime"));
		model.put("curDayTime", obj.get("curDayTime"));
		model.put("centerPoint", obj.get("centerPoint"));
//		System.out.println(obj.get("centerPoint"));
		logger.debug("index.res={}", res);
		return new ModelAndView("rno_4g_dynamic_coverage");
	}
	/**
	 * 
	* @Title: 根据父区域id，获取指定类型的子区域列表
	* @Description: 
	* @Company:  怡创科技
	* @return: void
	* @author chao_xj
	* @date 2016年6月3日
	 */
	@RequestMapping(value = "/getSubAreaByParentAreaForAjaxAction", method = RequestMethod.POST)
		String getSubAreaByParentAreaForAjaxAction(HttpServletRequest request,@RequestParam("parentAreaId") long parentAreaId,
												   @RequestParam("subAreaLevel") String subAreaLevel) {
		logger.debug("进入getSubAreaByParentAreaForAjaxAction方法,parentAreaId={},subAreaLevel={}",parentAreaId,subAreaLevel);
		if(subAreaLevel.equals("市")){
			subAreaLevel= "city";
		}else if(subAreaLevel.equals("区/县")){
			subAreaLevel= "country";
		}
		String account = request.getSession().getAttribute("account").toString();
		String result = restTemplate.getForObject(ServiceUrl+"getSubAreaByParentAreaForAjaxAction/{parentAreaId}/{subAreaLevel}/{account}",String.class,parentAreaId,subAreaLevel,account);
		logger.debug("getSubAreaByParentAreaForAjaxAction  返回result={}"+result);
		return result;
	}
	@RequestMapping(value = "/get4GDynaCoverageDataForAction", method = RequestMethod.POST)
	String get4GDynaCoverageDataForAction(
			@RequestParam("lteCellId") String lteCellId, @RequestParam("cityId") long cityId,
			@RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate,
			 @RequestParam("imgSizeCoeff") double imgSizeCoeff) {
		Map<String,Object> params =  new HashMap<String,Object>();
		params.put("lteCellId",lteCellId);
		params.put("cityId",cityId);
		params.put("startDate",startDate);
		params.put("endDate",endDate);
		params.put("imgSizeCoeff",imgSizeCoeff);
		String result = restTemplate.getForObject(ServiceUrl+"get4GDynaCoverageDataForAction/{lteCellId}/{cityId}/{startDate}/{endDate}/{imgSizeCoeff}",String.class,params);
		logger.debug("get4GDynaCoverageDataForAction  返回result=="+result);
		return result;
	}
	@RequestMapping(value = "/get4GDynaCoverageData2ForAction", method = RequestMethod.POST)
	String get4GDynaCoverageData2ForAction(
			@RequestParam("lteCellId") String lteCellId, @RequestParam("cityId") long cityId,
			@RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate,
			@RequestParam("imgCoeff") double imgCoeff, @RequestParam("imgSizeCoeff") double imgSizeCoeff) {
		Map<String,Object> params =  new HashMap<String,Object>();
		params.put("lteCellId",lteCellId);
		params.put("cityId",cityId);
		params.put("startDate",startDate);
		params.put("endDate",endDate);
		params.put("imgCoeff",imgCoeff);
		params.put("imgSizeCoeff",imgSizeCoeff);
		String result = restTemplate.getForObject(ServiceUrl+"get4GDynaCoverageData2ForAction/{lteCellId}/{cityId}/{startDate}/{endDate}/{imgCoeff}/{imgSizeCoeff}",String.class,params);
		logger.debug("get4GDynaCoverageData2ForAction  返回result=="+result);
		return result;
	}
	
	@RequestMapping(value = "/get4GDynaCoverageInInferDataForAction", method = RequestMethod.POST)
	String get4GDynaCoverageInInferDataForAction(
			@RequestParam("lteCellId") String lteCellId, @RequestParam("cityId") long cityId,
			@RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate) {
		Map<String,Object> params =  new HashMap<String,Object>();
		params.put("lteCellId",lteCellId);
		params.put("cityId",cityId);
		params.put("startDate",startDate);
		params.put("endDate",endDate);
		String result = restTemplate.getForObject(ServiceUrl+"get4GDynaCoverageInInferDataForAction/{lteCellId}/{cityId}/{startDate}/{endDate}",String.class,params);
		logger.debug("get4GDynaCoverageInInferDataForAction  返回result=="+result);
		return result;
	}
	@RequestMapping("/get4GDynaCoverageOutInferDataForAction")
	String get4GDynaCoverageOutInferDataForAction(
			@RequestParam("lteCellId") String lteCellId, @RequestParam("cityId") long cityId,
			@RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate) {
		Map<String,Object> params =  new HashMap<String,Object>();
		params.put("lteCellId",lteCellId);
		params.put("cityId",cityId);
		params.put("startDate",startDate);
		params.put("endDate",endDate);
		String result = restTemplate.getForObject(ServiceUrl+"get4GDynaCoverageOutInferDataForAction/{lteCellId}/{cityId}/{startDate}/{endDate}",String.class,params);
		logger.debug("get4GDynaCoverageOutInferDataForAction  返回result=="+result);
		return result;
	}
}
