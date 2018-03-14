package com.hgicreate.rno.controller;

import java.text.ParseException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;

@RestController
public class RnoPciEvaluateController {

	private static final Logger logger = LoggerFactory.getLogger(RnoPciEvaluateController.class);

	private String account = "liu.yp@iscreate.com";

	@Value("${service-url}")
	private String serviceUrl;

	@Autowired
	RestTemplate restTemplate;

	@RequestMapping(value = "/IN", method = RequestMethod.POST)
	String in(@RequestParam("cellId") String cellId, @RequestParam("jobId") int jobId) {
		logger.debug("cellId={},jobId={}, serviceUrl={}", cellId, jobId, serviceUrl + "/in");
		String json = restTemplate.getForObject(serviceUrl + "/in/{jobId}/{cellId}", String.class, jobId, cellId);
		logger.debug("IN.json={}", json);
		return json;
	}

	@RequestMapping(value = "/OUT", method = RequestMethod.POST)
	String out(@RequestParam("ncellId") String ncellId, @RequestParam("jobId") int jobId) {
		String json = restTemplate.getForObject(serviceUrl + "/out/{jobId}/{ncellId}", String.class, jobId, ncellId);
		return json;
	}

	@RequestMapping(value = "/PCI", method = RequestMethod.POST)
	String pci(@RequestParam("cellId") String cellId, @RequestParam("jobId") int jobId) {
		logger.debug("pci.cellId={},jobId={}", cellId, jobId);
		String json = restTemplate.getForObject(serviceUrl + "/pci/{jobId}/{cellId}", String.class, jobId, cellId);
		logger.debug("pci.json={}", json);
		return json;
	}

	/**
	 * 获取最近十次 LTE 干扰矩阵信息
	 */
	@RequestMapping(value = "/getLatelyLteMatrixByCityIdForAjaxAction", method = RequestMethod.POST)
	String getLatelyLteMatrixByCityIdForAjaxAction(@RequestParam("cityId") long cityId) {
		logger.debug("进入getLatelyLteMatrixByCityIdForAjaxAction方法.cityId={}", cityId);
		String result = restTemplate.getForObject(serviceUrl + "/getLatelyLteMatrixByCityIdForAjaxAction/{cityId}",
				String.class, cityId);
		return result;
	}

	/**
	 * 获取同站小区
	 */
	@RequestMapping(value = "/getSameStationCellsByLteCellIdForAjaxAction", method = RequestMethod.POST)
	String getSameStationCellsByLteCellIdForAjaxAction(@RequestParam("lteCell") String lteCell) {
		logger.debug("getSameStationCellsByLteCellIdForAjaxAction. lteCell={}", lteCell);
		String res = restTemplate.getForObject(serviceUrl + "/getSameStationCellsByLteCellIdForAjaxAction/{lteCell}",
				String.class, lteCell);
		return res;
	}

	/**
	 * 转换lte小区与某同站小区的pci
	 */
	@RequestMapping(value = "/changeLteCellPciForAjaxAction", method = RequestMethod.POST)
	String changeLteCellPciForAjaxAction(@RequestParam("cell1") String cell1, @RequestParam("cell2") String cell2,
			@RequestParam("pci1") String pci1, @RequestParam("pci2") String pci2) {
		logger.debug("changeLteCellPciForAjaxAction. cell1={}, pci1={}, cell2={}, pci2={}", cell1, pci1, cell2, pci2);
		String res = restTemplate.getForObject(
				serviceUrl + "/changeLteCellPciForAjaxAction/{cell1}/{pci1}/{cell2}/{pci2}", String.class, cell1, pci1,
				cell2, pci2);
		return res;
	}

	@RequestMapping("/")
	ModelAndView index(Map<String, Object> model, HttpServletRequest request, String account) throws ParseException {

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
		
		logger.debug("访问首页, account={}", account);
		
		String res = restTemplate.getForObject(serviceUrl + "/getArea/{account}", String.class, account);
		if(res==null) {
			model.put("error", "当前用户没有权限！");
			return new ModelAndView("rno_fail");
		}else {
			JSONObject obj = JSONObject.parseObject(res);
			model.put("provinces", obj.get("provinceAreas"));
			model.put("cities", obj.get("cityAreas"));
			model.put("countryAreas", obj.get("countryAreas"));
			logger.debug("index.res={}", res);
			return new ModelAndView("rno_pci_evaluate");
		}
	}

	/**
	 * 
	 * @Title: 根据父区域id，获取指定类型的子区域列表
	 * @Description:
	 * @Company: 怡创科技
	 * @param request
	 * @return: List<Area>
	 * @author chao_xj
	 * @date 2016年6月3日
	 */
	@RequestMapping(value = "/getSubAreaByParentAreaForAjaxAction", method = RequestMethod.POST)
	String getSubAreaByParentAreaForAjaxAction(@RequestParam("parentAreaId") long parentAreaId,
			@RequestParam("subAreaLevel") String subAreaLevel) {
		logger.debug("进入getSubAreaByParentAreaForAjaxAction方法,parentAreaId={},subAreaLevel={}", parentAreaId,
				subAreaLevel);
		if(subAreaLevel.equals("市")){
			subAreaLevel= "city";
		}else if(subAreaLevel.equals("区/县")){
			subAreaLevel= "country";
		}
		String res = restTemplate.getForObject(serviceUrl + "/getSubAreaByParentAreaForAjaxAction/{parentAreaId}/{subAreaLevel}/{account}",
				String.class, parentAreaId, subAreaLevel, account);
		logger.debug("getSubAreaByParentAreaForAjaxAction,res={}",res);
		return res;
	}

}
