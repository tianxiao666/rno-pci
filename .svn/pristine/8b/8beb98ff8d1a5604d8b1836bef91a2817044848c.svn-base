package com.iscreate.rno.microservice.pci.afp.task;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iscreate.rno.microservice.pci.afp.dao.RnoPciAfpDao;
import com.iscreate.rno.microservice.pci.afp.mapper.oracle.OracleMapper;
import com.iscreate.rno.microservice.pci.afp.mapper.spark.SparkMapper;
import com.iscreate.rno.microservice.pci.afp.model.DataParseStatus;
import com.iscreate.rno.microservice.pci.afp.model.JobParseStatus;
import com.iscreate.rno.microservice.pci.afp.model.JobProfile;
import com.iscreate.rno.microservice.pci.afp.model.Report;

public class NewPciJob {
	private static final Logger logger = LoggerFactory.getLogger(NewPciJob.class);

	private long jobId;

	private OracleMapper oracleMapper;

	private RnoPciAfpDao rnoPciAfpDao;
	
	private SparkMapper sparkMapper;

	public NewPciJob(long jobId, OracleMapper oracleMapper, SparkMapper sparkMapper, RnoPciAfpDao rnoPciAfpDao) {
		super();
		this.jobId = jobId;
		this.oracleMapper = oracleMapper;
		this.sparkMapper = sparkMapper;
		this.rnoPciAfpDao = rnoPciAfpDao;
	}

	public boolean run() {
		JobProfile job = oracleMapper.getJobNameByJobId(jobId);
		String msg = "";
		Date startTime = new Date();
		NewPciConfig config = new NewPciConfig(jobId, oracleMapper, sparkMapper, rnoPciAfpDao);

		updateOwnProgress("正在计算");
		if (!config.buildPciTaskConf()) {
			msg = "+++>>>名称=" + job.getJobName() + ",结果：任务失败！原因：初始化失败。";
			logger.error(msg);
			// 保存报告信息
			addReport(startTime, msg, DataParseStatus.Fail.toString(), "任务初始化");
			updateOwnProgress("计算失败");
			updateJobEndTime(job, JobParseStatus.Fail.toString());
			return false;
		}
		
		// 读取结果
		if (!config.readData()) {
			msg = "名称 = " + job.getJobName() + ", 结果：任务失败！原因：读取干扰数据失败！";
			logger.error(msg);
			// 保存报告信息
			addReport(startTime, msg, DataParseStatus.Fail.toString(), "读取数据");
			updateOwnProgress("计算失败");
			updateJobEndTime(job, JobParseStatus.Fail.toString());
			return false;
		}
		// 开始计算
		NewPciCalc pciCalc = new NewPciCalc(config, oracleMapper);
		try {
			pciCalc.execCalc();
		} catch (Exception e) {
			e.printStackTrace();
			msg = "名称 = " + job.getJobName() + ", 结果：任务失败！原因：计算失败！";
			logger.error(msg);
			// 保存报告信息
			addReport(startTime, msg, DataParseStatus.Fail.toString(), "优化计算");
			updateOwnProgress("计算失败");
			updateJobEndTime(job, JobParseStatus.Fail.toString());
			return false;
		}
		// 结束任务
		if (!"fail".equals(config.getReturnInfo())) {
			msg = "名称 = " + job.getJobName() + ", 结果：任务成功！<br>返回信息：" + config.getReturnInfo();
		} else {
			msg = "名称 = " + job.getJobName() + ", 结果：任务成功！<br>读取返回信息失败！";
		}
		logger.info(msg);
		// 保存报告信息
		addReport(startTime, msg, DataParseStatus.Suc.toString(), "优化计算");
		updateOwnProgress("计算完成");
		updateJobEndTime(job, JobParseStatus.Succeded.toString());
		return true;
	}

	private void updateOwnProgress(String jobStatus) {
		// 更新pci规划干扰计算表的进度
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("jobId", jobId);
		map.put("jobStatus", jobStatus);
		oracleMapper.updatePciPlanWorkStatus(map);
	}

	/**
	 * 添加报告到数据库
	 */
	private void addReport(Date date1, String msg, String status, String stage) {
		Date date2;
		Report report = new Report();
		date2 = new Date();
		report.setJobId(jobId);
		report.setBegTime(date1);
		report.setEndTime(date2);
		report.setFinishState(status);
		report.setStage(stage);
		report.setAttMsg(msg);
		oracleMapper.addReport(report);
	}

	/**
	 * 更新任务状态
	 */
	private void updateJobEndTime(JobProfile job, String jobStatus) {
		job.setJobStateStr(jobStatus);
		job.setFinishTime(new Date());
		oracleMapper.updateJobEndTime(job);
	}

	/** 清理任务 **/
	public void clean() {
		oracleMapper = null;

		rnoPciAfpDao = null;
	}
}
