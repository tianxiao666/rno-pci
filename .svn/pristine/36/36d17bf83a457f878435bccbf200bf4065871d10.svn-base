package com.hgicreate.rno.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hgicreate.rno.service.ServiceForPciEvalService;

@CrossOrigin
@RestController
public class ServiceForPciEvalController {

	private static final Logger logger = LoggerFactory.getLogger(ServiceForPciEvalController.class);
	
	@Autowired
	private ServiceForPciEvalService serviceForPciEvalService;

	@RequestMapping("/in/{jobId}/{cellId}")
	List<Map<String, Object>> in(@PathVariable("jobId") int jobId, @PathVariable("cellId") String cellId) {
		logger.debug("jobId={},cellId={}", cellId, jobId);
		return serviceForPciEvalService.getInCellInfo(jobId, cellId);
	}

	@RequestMapping("/out/{jobId}/{ncellId}")
	List<Map<String, Object>> out(@PathVariable("jobId") int jobId, @PathVariable("ncellId") String ncellId) {
		logger.debug("jobId={},ncellId={}", jobId, ncellId);
		return serviceForPciEvalService.getOutCellInfo(jobId, ncellId);
	}

	@RequestMapping("/pci/{jobId}/{cellId}")
	List<Map<String, Object>> pci(@PathVariable("jobId") int jobId, @PathVariable("cellId") String cellId) {
		logger.debug("jobId={},cellId={}", cellId, jobId);
		return serviceForPciEvalService.getPciCellInfo(jobId, cellId);
	}

	/**
	 * 获取最近十次 LTE 干扰矩阵信息
	 */
	@RequestMapping("/getLatelyLteMatrixByCityIdForAjaxAction/{cityId}")
	List<Map<String, Object>> getLatelyLteMatrixByCityIdForAjaxAction(@PathVariable("cityId") long cityId) {
		logger.debug("进入getLatelyLteMatrixByCityIdForAjaxAction方法,cityId={}", cityId);
		return serviceForPciEvalService.getLatelyLteMatrixByCityId(cityId);
	}

	/**
	 * 获取同站小区
	 */
	@RequestMapping("/getSameStationCellsByLteCellIdForAjaxAction/{lteCell}")
	List<Map<String, String>> getSameStationCellsByLteCellIdForAjaxAction(@PathVariable("lteCell") String lteCell) {
		logger.info("getSameStationCellsByLteCellIdForAjaxAction. lteCell={}", lteCell);
		return serviceForPciEvalService.getSameStationCellsByLteCellId(lteCell);
	}

	/**
	 * 转换lte小区与某同站小区的pci
	 */
	@RequestMapping("/changeLteCellPciForAjaxAction/{cell1}/{pci1}/{cell2}/{pci2}")
	Map<String, Boolean> changeLteCellPciForAjaxAction(@PathVariable("cell1") String cell1,
			@PathVariable("cell2") String cell2, @PathVariable("pci1") String pci1, @PathVariable("pci2") String pci2) {
		logger.debug("changeLteCellPciForAjaxAction. cell1={}, pci1={}, cell2={}, pci2={}", cell1, pci1, cell2, pci2);
		return serviceForPciEvalService.changeLteCellPci(cell1, pci1, cell2, pci2);
	}
	
	/**
	 * 获取区域
	 */
	@RequestMapping("/getArea/{account:.+}")
	Map<String, List<Map<String, Object>>> index(@PathVariable("account") String account) {
		logger.debug("getArea.account={}", account);
		Map<String, List<Map<String, Object>>> map;
		try {
		    map = serviceForPciEvalService.getAreaByAccount(account);
		} catch (Exception e) {
			return null;
		}
		return map;
	}
	
	/**
	 * 根据父区域id，获取指定类型的子区域列表
	 */
	@RequestMapping("/getSubAreaByParentAreaForAjaxAction/{parentAreaId}/{subAreaLevel}/{account:.+}")
	List<Map<String, Object>> getSubAreaByParentAreaForAjaxAction(@PathVariable("parentAreaId") long parentAreaId,@PathVariable("subAreaLevel") String subAreaLevel, @PathVariable("account") String account) {
		logger.debug("进入getSubAreaByParentAreaForAjaxAction方法,parentAreaId={},subAreaLevel={},account={}",parentAreaId,subAreaLevel,account);
		if(subAreaLevel.equals("city")){
			subAreaLevel = "市";
		} else if(subAreaLevel.equals("country")){
			subAreaLevel = "区/县";
		}
		return serviceForPciEvalService.getSpecialSubAreasByAccountAndParentArea(account, parentAreaId, subAreaLevel);
	}

}
