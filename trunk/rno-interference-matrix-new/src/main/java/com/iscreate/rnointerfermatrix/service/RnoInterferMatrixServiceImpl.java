package com.iscreate.rnointerfermatrix.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.iscreate.rnointerfermatrix.dao.RnoInterferMatrixDao;
import com.iscreate.rnointerfermatrix.mapper.oracle.OracleMapper;
import com.iscreate.rnointerfermatrix.mapper.spark.SparkMapper;
import com.iscreate.rnointerfermatrix.model.G4HoDescQueryCond;
import com.iscreate.rnointerfermatrix.model.G4MrDescQueryCond;
import com.iscreate.rnointerfermatrix.model.G4SfDescQueryCond;
import com.iscreate.rnointerfermatrix.model.JobProfile;
import com.iscreate.rnointerfermatrix.model.Page;
import com.iscreate.rnointerfermatrix.model.RnoLteInterMatrixTaskInfo;

@Service("rnoInterferMatrixServiceImpl")
public class RnoInterferMatrixServiceImpl implements RnoInterferMatrixService {

	private static final Logger logger = LoggerFactory.getLogger(RnoInterferMatrixServiceImpl.class);

	@Autowired
	@Qualifier("rnoInterferMatrixDaoImpl")
	private RnoInterferMatrixDao rnoInterferMatrixDao;

	@Autowired
	private OracleMapper oracleMapper;

	@Autowired
	private SparkMapper sparkMapper;

	/**
	 * @title 分页查询4g干扰矩阵信息
	 * @param condition
	 * @param page
	 * @return
	 */
	@Override
	public List<Map<String, Object>> queryLteInterferMartixByPage(Map<String, Object> condition, Page page) {
		logger.debug("进入方法：queryLteInterferMartixByPage。condition=" + condition + ",page=" + page);
		if (page == null) {
			logger.warn("方法：queryLteInterferMartixByPage的参数：page 是空！无法查询!");
			return Collections.emptyList();
		}
		long totalCnt = page.getTotalCnt();
		if (totalCnt < 0) {
			totalCnt = rnoInterferMatrixDao.getLteInterferMartixCount(condition);
			page.setTotalCnt((int) totalCnt);
		}
		int startIndex = page.calculateStart();
		int cnt = page.getPageSize();
		Map<String, Object> map = condition;
		map.put("startIndex", startIndex);
		map.put("cnt", cnt);
		List<Map<String, Object>> res = rnoInterferMatrixDao.queryLteInterferMartixByPage(map);
		return res;
	}

	/**
	 * @title 检查这周是否计算过4g pci干扰矩阵
	 * @param areaId
	 * @param thisMonday
	 * @param today
	 * @return
	 */
	public List<Map<String, Object>> checkLteInterMartixThisWeek(long areaId, String thisMonday, String today) {
		return rnoInterferMatrixDao.checkLteInterMartixThisWeek(areaId, thisMonday, today);
	}

	/**
	 * @title 获取任务名列表
	 * @param attachParams
	 * @return
	 */
	public List<String> queryTaskNameListByCityId(long cityId) {
		List<Map<String, String>> taskNameList = new ArrayList<Map<String, String>>();
		List<String> result = new ArrayList<String>();
		taskNameList = rnoInterferMatrixDao.getTaskNameListByCityId(cityId);
		for (Map<String, String> map : taskNameList) {
			result.add(map.get("TASK_NAME").trim());
		}
		return result;
	}

	/**
	 * 
	 * @title 新增4g pci 干扰矩阵
	 * @param taskInfo
	 */
	public boolean addLteInterMartix(final RnoLteInterMatrixTaskInfo taskInfo) {
		logger.debug("进入方法：addLteInterMartix。taskInfo={}", taskInfo);
		// 数据量
		int mrRecordNum = rnoInterferMatrixDao.queryMrDataRecordsCount(taskInfo.getCityId(), taskInfo.getBegTime(),
				taskInfo.getEndTime());
		int hoRecordNum = rnoInterferMatrixDao.queryHoDataRecordsCount(taskInfo.getCityId(), taskInfo.getBegTime(),
				taskInfo.getEndTime());
		long sfRecordNum = taskInfo.getSfFileCounts();
		String dataDescription = "";
		long recordNum = 0;

		recordNum = mrRecordNum + hoRecordNum + sfRecordNum;
		dataDescription = "MR:" + mrRecordNum + ";HO:" + hoRecordNum + ";SF:" + sfRecordNum;

		taskInfo.setDataDescription(dataDescription);
		taskInfo.setRecordNum(recordNum);

		JobProfile job = new JobProfile();
		job.setAccount(taskInfo.getAccount());
		job.setJobName("计算4gpci干扰矩阵新算法");
		job.setJobType(taskInfo.getJobType());
		job.setSubmitTime(new Date());
		job.setDescription("4g pci干扰矩阵job 新算法");
		job.setJobStateStr("Waiting");
		oracleMapper.addJob(job);

		// 获取jobId
		long jobId = job.getJobId();
		if (jobId > 0) {
			// 在数据库创建干扰矩阵计算任务
			boolean flag = rnoInterferMatrixDao.createLteInterMartixRec(jobId, taskInfo);
			if (!flag) {
				logger.error("jobId=" + jobId + " 的4g pci 干扰矩阵任务创建失败");
				return false;
			}
			return true;
		} else {
			return false;
		}
	}

	@Override
	public List<Map<String, Object>> queryMrDataFromHbaseByPage(G4MrDescQueryCond cond, Page newPage) {
		return rnoInterferMatrixDao.queryMrDataFromHbaseByPage(cond, newPage);
	}

	@Override
	public List<Map<String, Object>> queryHoDataFromHbaseByPage(G4HoDescQueryCond cond, Page newPage) {
		return rnoInterferMatrixDao.queryHoDataFromHbaseByPage(cond, newPage);
	}

	@Override
	public List<Map<String, Object>> querySfDataFromHbaseByPage(G4SfDescQueryCond cond, Page newPage) {
		return rnoInterferMatrixDao.querySfDataFromHbaseByPage(cond, newPage);
	}

	@Override
	public int queryMrDataCountByCond(RnoLteInterMatrixTaskInfo taskInfo) {
		Map<String, Object> map = new HashMap<>();
		map.put("beginTime", taskInfo.getBegTime().replace("-", ""));
		map.put("endTime", taskInfo.getEndTime().replace("-", ""));
		map.put("areaId", taskInfo.getCityId());
		return sparkMapper.getMrcount(map);
	}

	@Override
	public int queryHoDataCountByCond(RnoLteInterMatrixTaskInfo taskInfo) {
		Map<String, Object> map = new HashMap<>();
		map.put("beginTime", taskInfo.getBegTime().replace("-", ""));
		map.put("endTime", taskInfo.getEndTime().replace("-", ""));
		map.put("areaId", taskInfo.getCityId());
		return sparkMapper.getHocount(map);
	}

}
