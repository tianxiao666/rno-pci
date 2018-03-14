package com.hgicreate.rno.service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.hgicreate.rno.dao.Rno4GAzimuthCalcDao;
import com.hgicreate.rno.model.JobParseStatus;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.Cluster;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.JobID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.hgicreate.rno.mapper.G4AzimuthCalcMapper;
import com.hgicreate.rno.model.G4SfDescQueryCond;
import com.hgicreate.rno.model.JobProfile;
import com.hgicreate.rno.model.MrJobCond;
import com.hgicreate.rno.model.Page;
import com.hgicreate.rno.tool.DateUtil;

@Service("rno4GAzimuthCalcServiceImpl")
public class Rno4GAzimuthCalcServiceImpl implements Rno4GAzimuthCalcService {

	private static final Logger logger = LoggerFactory.getLogger(Rno4GAzimuthCalcServiceImpl.class);

	@Autowired
	private G4AzimuthCalcMapper g4AzimuthCalcMapper;

	@Autowired
	@Qualifier("rno4GAzimuthCalcDaoImpl")
	private Rno4GAzimuthCalcDao rno4GAzimuthCalcDao;
	
	@Resource(name = "hadoopConfig")
	private Configuration config;
	/**
	 * 停止任务
	 */
	@Override
	public boolean stopJobByJobIdAndMrJobId(MrJobCond mrJobCond) {
		logger.debug("进入方法：stopJobByJobIdAndMrJobId。cond={}", mrJobCond);
		boolean flag = true;

		@SuppressWarnings("unused")
		long jobId = mrJobCond.getJobId();
		String mrJobId = mrJobCond.getMrJobId();
		String runType = mrJobCond.getRunType();
		@SuppressWarnings("unused")
		String account = mrJobCond.getAccount();

		
		// 停止Hadoop的mapreduce的job
		if ("hadoop".equals(runType)) {
		try {
//		Job mrJob = new Cluster(new Configuration()).getJob(JobID.forName(mrJobId));
		Job mrJob = new Cluster(config).getJob(JobID.forName(mrJobId));
		if (mrJob != null) {
		logger.debug("通过mrJobId从session获取的job是:" + mrJob);
		mrJob.killJob();
		logger.debug(mrJob + "该mr工作状态为：" + mrJob.getJobState());
		}
		} catch (IOException e) {
		logger.error(mrJobId + "该mrjob停止失败!");
		flag = false;
		e.printStackTrace();
		} catch (InterruptedException e) {
		logger.error(mrJobId + "该mrjob停止失败!");
		flag = false;
		e.printStackTrace();
		}
		}
		return flag;
	}


	@Override
	public Map<String, List<String>> getLteCellInfoByCellId(long cityId) {
		return rno4GAzimuthCalcDao.getLteCellInfoByCellId(cityId);
	}
	/*
	 * 查询4g方位角计算任务
	 */
	public List<Map<String, Object>> query4GAzimuthCalcTaskByPage(
			Map<String, String> cond, Page page, String account) {
		logger.info("进入方法：query4GAzimuthCalcTaskByPage。condition=" + cond
				+ ",page=" + page);
		if (page == null) {
			logger.warn("方法：query4GAzimuthCalcTaskByPage的参数：page 是空！无法查询!");
			return Collections.emptyList();
		}
		long totalCnt = page.getTotalCnt();
		if (totalCnt < 0) {
			totalCnt = rno4GAzimuthCalcDao.get4GAzimuthCalcTaskCount(cond, account);
			page.setTotalCnt((int) totalCnt);
		}
		int startIndex = page.calculateStart();
		int cnt = page.getPageSize();
		List<Map<String, Object>> res = rno4GAzimuthCalcDao.query4GAzimuthCalcTaskByPage(cond, account, startIndex, cnt);
				
		return res;
	}
	/*
	 * 提交4g方位角计算任务
	 */
	public Map<String, Object> submit4GAzimuthCalcTask(String account,
			final Map<String, Object> taskInfo) {
		logger.debug("进入submit4GAzimuthCalcTask account=" + account
				+  ",taskInfo=" + taskInfo);
		Map<String, Object> result = new HashMap<String, Object>();
		// 创建job
		JobProfile job = new JobProfile();
		job.setAccount(account);
		job.setJobName(taskInfo.get("TASKNAME").toString());
		job.setJobType("RNO_4G_AZIMUTH_CALC");
		
		job.setSubmitTime(new Date());
		job.setDescription(taskInfo.get("TASKDESC").toString());
		job.setJobStateStr(JobParseStatus.Waiting.toString());
		/*对rno_ms_job表插入一条job*/
		g4AzimuthCalcMapper.addJob(job);
		
		long jobId = job.getJobId();
		if (jobId == 0) {
			logger.error("创建jobId失败！");
			result.put("flag", false);
			result.put("desc", "提交任务失败！");
		}
		// 保存4g方位角任务
		final String begMeaTime = taskInfo.get("STARTTIME").toString();
		final String endMeaTime = taskInfo.get("ENDTIME").toString();
		final long cityId = Long.parseLong(taskInfo.get("CITYID").toString());
		
		// 下载文件名
		String dlFileName = jobId + "_4G方位角对比表.xlsx";
		// 读取文件名
		String rdFileName = jobId + "_4g_azimuth_data";
		// 创建日期
		Calendar cal = Calendar.getInstance();
		cal = Calendar.getInstance();
		String createTime = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss").format(cal.getTime());
//		Configuration config = new Configuration();
		// 文件保存路径
		String resultDir = config.get("fs.defaultFS")+"/user/"+config.get("hadoop.user.name")+"/rno_data/4g_azimuth/" + cal.get(Calendar.YEAR) 
				+ "/" + (cal.get(Calendar.MONTH) + 1);
		String finishState = "排队中";
		// 更新日期
		String modTime = createTime;
		Map<String, String> cond = new HashMap<String, String>();
		cond.put("jobId", jobId+"");
		cond.put("begMeaTime", begMeaTime);
		cond.put("endMeaTime", endMeaTime);
		cond.put("cityId", cityId+"");
		cond.put("dlFileName", dlFileName);
		cond.put("rdFileName", rdFileName);
		cond.put("createTime", createTime);
		cond.put("modTime", modTime);
		cond.put("finishState", finishState);
		cond.put("resultDir", resultDir);
		
		// 执行
		try {
			g4AzimuthCalcMapper.save4GAzimuthJob(cond);
			result.put("flag", true);
			result.put("jobId", jobId);
			result.put("desc", "提交任务成功！");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("jobId=" + jobId + "，保存4g方位角计算任务失败！");
			result.put("flag", false);
			result.put("desc", "提交任务失败！");
		}finally {
			
		}
		logger.debug("退出submit4GAzimuthCalcTask result=" + result);
		return result;
	}
	/**
	 * 通过 jobId 获取4g方位角计算记录信息
	 */
	public List<Map<String, Object>> query4GAzimuthJobRecByJobId(long jobId){
		return g4AzimuthCalcMapper.query4GAzimuthJobRecByJobId(jobId);
	}
	/**
	 * 通过 jobId 获取网络覆盖计算记录信息
	 */
	public List<Map<String, Object>> queryNetWorkCoverResByJobId(long jobId){
		return g4AzimuthCalcMapper.queryNetWorkCoverResByJobId(jobId);
	}
}
