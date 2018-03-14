package com.hgicreate.rno.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hgicreate.rno.model.Page;
import com.hgicreate.rno.service.RnoCommonService;
import com.hgicreate.rno.service.RnoLteDynaOverGraphService;
import com.hgicreate.rno.tool.CacheHelper;
import com.hgicreate.rno.model.Area;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.*;

@CrossOrigin
//@Controller
@RestController
public class RnoLteDynaOverGraphController {

	private static final Logger logger = LoggerFactory.getLogger(RnoLteDynaOverGraphController.class);

	private static Gson gson = new GsonBuilder().create();
	
	private Page page;// 分页类
	// 地图网格
//	private Map<String, String> mapGrid;

	private String account = "";

	@Autowired
	@Qualifier("rnoCommonServiceImpl")
	private RnoCommonService rnoCommonService;

	@Autowired
	@Qualifier("rnoLteDynaOverGraphServiceImpl")
	private RnoLteDynaOverGraphService rnoLteDynaOverGraphService;

	private List<Area> provincesList = new ArrayList<Area>();

	private List<Area> citiesList = new ArrayList<Area>();
	
	private List<Area> countryAreas = new ArrayList<Area>();
	
	private List<Area> centerPoint = new ArrayList<Area>();

	private String allAreas;

	@Autowired
	@Qualifier("cacheHelper")
	private CacheHelper cacheHelper;
	/**
	 * 获取用户权限下的区域
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping("/getAreas/{account:.+}")
//	Map<String, Object> index(Map<String, Object> model) {
	Map<String, Object> index(@PathVariable("account") String account) {
		logger.debug("访问首页");
		Map<String, Object> model = new HashMap<String,Object>();
		Map<String, List<Area>> map = rnoCommonService.getAreaByAccount(account);
		provincesList = map.get("provinceAreas");
		citiesList = map.get("cityAreas");
		countryAreas = map.get("countryAreas");
		centerPoint = map.get("centerPoint");
		model.put("provinces", provincesList);
		model.put("cities", citiesList);
		model.put("countryAreas", countryAreas);
		model.put("centerPoint", centerPoint);
		allAreas = gson.toJson(map.get("allAreas"));
		model.put("allAreas", allAreas);
		Calendar cal = Calendar.getInstance();
	    cal.add(Calendar.DATE,-5);
	    String preFiveDayTime = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
	    model.put("preFiveDayTime", preFiveDayTime);
	    String curDayTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
	    model.put("curDayTime", curDayTime);
		logger.debug("provinceAreas={}, city={}, allAreas={}", provincesList, citiesList, allAreas);
		return model;
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
/*	@RequestMapping("/get4GDynaCoverageDataForAction/{lteCellId}/{cityId}/{startDate}/{endDate}/{imgSizeCoeff}")
	Map<String, List<Map<String, Object>>> get4GDynaCoverageDataForAction(
			@PathVariable("lteCellId") String lteCellId, @PathVariable("cityId") long cityId,
			@PathVariable("startDate") String startDate, @PathVariable("endDate") String endDate,
			 @PathVariable("imgSizeCoeff") double imgSizeCoeff) {
		logger.debug("获取画LTE小区动态覆盖图(贝塞尔曲线)所需的数据, lteCellId=" + lteCellId
				+ ", cityId=" + cityId + ", startDate=" + startDate
				+ ", endDate=" + endDate + ",imgSizeCoeff="+imgSizeCoeff);
		

		Map<String, List<Map<String, Object>>> res = rnoLteDynaOverGraphService
				.get4GDynaCoverageDataByCityAndDate(lteCellId, cityId, startDate, endDate,imgSizeCoeff);
		return res;
	}*/



	@RequestMapping("/get4GDynaCoverageData2ForAction/{lteCellId}/{cityId}/{startDate}/{endDate}/{imgCoeff}/{imgSizeCoeff}")
	Map<String, List<Map<String, Object>>> get4GDynaCoverageData2ForAction(
			@PathVariable("lteCellId") String lteCellId, @PathVariable("cityId") long cityId,
			@PathVariable("startDate") String startDate, @PathVariable("endDate") String endDate,
			@PathVariable("imgCoeff") double imgCoeff, @PathVariable("imgSizeCoeff") double imgSizeCoeff) {
		logger.debug("获取画LTE小区动态覆盖图(折线)所需的数据, lteCellId=" + lteCellId
				+ ", cityId=" + cityId + ", startDate=" + startDate
				+ ", endDate=" + endDate + ", imgCoeff=" + imgCoeff+",imgSizeCoeff="+imgSizeCoeff);

		Map<String, List<Map<String, Object>>> res = rnoLteDynaOverGraphService
				.get4GDynaCoverageData2ByCityAndDate(lteCellId, cityId, startDate, endDate,imgCoeff,imgSizeCoeff);
		return res;
	}
	
	@RequestMapping("/get4GDynaCoverageInInferDataForAction/{lteCellId}/{cityId}/{startDate}/{endDate}")
	List<Map<String, String>> get4GDynaCoverageInInferDataForAction(
			@PathVariable("lteCellId") String lteCellId, @PathVariable("cityId") long cityId,
			@PathVariable("startDate") String startDate, @PathVariable("endDate") String endDate) {
		logger.debug("获取画LTE小区动态覆盖图(折线)所需的数据 in 干扰, lteCellId=" + lteCellId
				+ ", cityId=" + cityId + ", startDate=" + startDate
				+ ", endDate=" + endDate );

		List<Map<String, String>> res = rnoLteDynaOverGraphService
				.get4GDynaCoverageInInferDataByCityAndDate(lteCellId, cityId, startDate, endDate);
		return res;
	}
	@RequestMapping("/get4GDynaCoverageOutInferDataForAction/{lteCellId}/{cityId}/{startDate}/{endDate}")
	List<Map<String, String>> get4GDynaCoverageOutInferDataForAction(
			@PathVariable("lteCellId") String lteCellId, @PathVariable("cityId") long cityId,
			@PathVariable("startDate") String startDate, @PathVariable("endDate") String endDate) {
		logger.debug("获取画LTE小区动态覆盖图(折线)所需的数据out  干扰, lteCellId=" + lteCellId
				+ ", cityId=" + cityId + ", startDate=" + startDate
				+ ", endDate=" + endDate);
		
		List<Map<String, String>> res = rnoLteDynaOverGraphService
				.get4GDynaCoverageOutInferDataByCityAndDate(lteCellId, cityId, startDate, endDate);
		return res;
	}
}
