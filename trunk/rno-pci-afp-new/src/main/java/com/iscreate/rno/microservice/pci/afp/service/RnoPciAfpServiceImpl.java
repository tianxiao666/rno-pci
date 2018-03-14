package com.iscreate.rno.microservice.pci.afp.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.iscreate.rno.microservice.pci.afp.dao.RnoPciAfpDao;
import com.iscreate.rno.microservice.pci.afp.mapper.oracle.OracleMapper;
import com.iscreate.rno.microservice.pci.afp.model.G4SfDescQueryCond;
import com.iscreate.rno.microservice.pci.afp.model.Page;
import com.iscreate.rno.microservice.pci.afp.model.RnoLteInterferCalcTask;
import com.iscreate.rno.microservice.pci.afp.model.RnoThreshold;
import com.iscreate.rno.microservice.pci.afp.model.TaskCond;
import com.iscreate.rno.microservice.pci.afp.tool.DateUtil;

@Service("rnoPciAfpServiceImpl")
public class RnoPciAfpServiceImpl implements RnoPciAfpService {

	private static final Logger logger = LoggerFactory.getLogger(RnoPciAfpServiceImpl.class);

	@Autowired
	private OracleMapper oracleMapper;

	@Autowired
	@Qualifier("rnoPciAfpDaoImpl")
	private RnoPciAfpDao rnoPciAfpDao;
	
	/**
	 * 获取最近十次 LTE 干扰矩阵信息
	 */
	@Override
	public List<Map<String, Object>> getLatelyLteMatrixByCityId(long cityId) {
		return oracleMapper.getLatelyLteMatrixByCityId(cityId);
	}

	@Override
	public List<RnoThreshold> getThresholdsByModuleType(String moduleType) {
		return oracleMapper.getThresholdsByModuleType(moduleType);
	}

	/**
	 * 
	 * @title 通过区域及起始时间和厂家,及数据类型从Hbase获取MR数据描述记录情况
	 * @param cityId
	 * @param startTime
	 * @param endTime
	 * @param factory
	 *            ERI,ZTE
	 * @return
	 */
	@Override
	public List<Map<String, Object>> getDataRecordFromHbase(long cityId, String startTime, String endTime,
			String factory) {

		DateUtil dateUtil = new DateUtil();

		if (StringUtils.isBlank(startTime) || StringUtils.isBlank(endTime)) {
			logger.error("getDataRecordFromHbase未指明时间范围！");
			return Collections.emptyList();
		}
		Date startDate = dateUtil.to_yyyyMMddDate(startTime);
		Date endDate = dateUtil.to_yyyyMMddDate(endTime);

		if (startDate == null || endDate == null) {
			logger.error("getDataRecordFromHbase指定的时间有错！startTime={},endTime={}", startTime, endTime);
			return Collections.emptyList();
		}
		List<Date> dateList = DateUtil.findDates(startDate, endDate);
		// MR
		List<Map<String, Object>> mrRecordNumList = rnoPciAfpDao.getDataDescRecordFromHbase(cityId, startTime, endTime,
				factory, "MR");
		// HO
		List<Map<String, Object>> hoRecordNumList = rnoPciAfpDao.getDataDescRecordFromHbase(cityId, startTime, endTime,
				factory, "HO");
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		Map<String, Object> map;
		String date = "";
		// long recordNum;
		String meaTime = "";

		for (Date d : dateList) {
			date = dateUtil.format_yyyyMMdd(d);
			map = new HashMap<String, Object>();
			map.put("DATETIME", date);
			map.put("MR_RECORD_NUM", "--");
			map.put("HO_RECORD_NUM", "--");
			for (Map<String, Object> one : mrRecordNumList) {
				meaTime = one.get("MEA_TIME").toString();
				meaTime = dateUtil.format_yyyyMMdd(dateUtil.parseDateArbitrary(meaTime));
				if (meaTime.equals(date)) {
					map.put("MR_RECORD_NUM", Long.parseLong(one.get("RECORD_COUNT").toString()));
				}
			}
			for (Map<String, Object> one : hoRecordNumList) {
				meaTime = one.get("MEA_TIME").toString();
				meaTime = dateUtil.format_yyyyMMdd(dateUtil.to_yyyyMMddDate(meaTime));
				if (meaTime.equals(date)) {
					map.put("HO_RECORD_NUM", Long.parseLong(one.get("RECORD_COUNT").toString()));
				}
			}
			result.add(map);
		}
		return result;
	}

	@Override
	public List<Map<String, String>> querySfDataFromHbaseByPage(G4SfDescQueryCond cond) {
		return rnoPciAfpDao.querySfDataFromHbaseByPage(cond);
	}

	/**
	 * 查询pci自动规划任务
	 * 
	 */
	@Override
	public List<Map<String, Object>> queryPciPlanTaskByPage(TaskCond cond, Page page) {
		logger.debug("进入方法：queryPciPlanTaskByPage。condition={}, page={}", cond, page);

		long totalCnt = page.getTotalCnt();
		if (totalCnt < 0) {
			totalCnt = rnoPciAfpDao.getPciAnalysisTaskCount(cond);
			page.setTotalCnt((int)totalCnt);
		}
		int startIndex = page.calculateStart();
		int cnt = page.getPageSize();
		cond.setStartIndex(startIndex);
		cond.setCnt(cnt);
		List<Map<String, Object>> res = rnoPciAfpDao.queryPciPlanTaskByPage(cond);
		return res;
	}

	/**
	 * 停止任务
	 */
	@Override
	public boolean stopJob(long jobId) {
		logger.debug("进入方法：stopJob。jobId={}", jobId);
		boolean flag = false;
		Date date = new Date();
		String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date.getTime());
		if(oracleMapper.stopJob(jobId, now) > 0) {
			flag = true;
		}
		return flag;
	}

	/**
	 * 提交PCI规划任务
	 * 
	 * @param account
	 * @param threshold
	 * @param taskInfo
	 * @return
	 */
	@Override
	public Map<String, Object> submitPciPlanAnalysisTask(long jobId, String account,
			final List<RnoThreshold> rnoThresholds, final RnoLteInterferCalcTask.TaskInfo taskInfo) {
		logger.debug("进入submitPciPlanAnalysisTask account={},rnoThresholds={},taskInfo={}", account, rnoThresholds,
				taskInfo);
		Map<String, Object> result = new HashMap<String, Object>();
		
		result = rnoPciAfpDao.savePciPlanAnalysisTaskInfo(jobId, rnoThresholds, taskInfo);

		return result;
	}

	@Override
	public Map<String, List<String>> getLteCellInfoByCellId(long cityId) {
		return rnoPciAfpDao.getLteCellInfoByCellId(cityId);
	}

}
