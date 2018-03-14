package com.iscreate.rnointerfermatrix.schedule;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.iscreate.rnointerfermatrix.mapper.oracle.OracleMapper;
import com.iscreate.rnointerfermatrix.model.JobParseStatus;
import com.iscreate.rnointerfermatrix.model.JobProfile;
import com.iscreate.rnointerfermatrix.properties.RnoProperties;
import com.iscreate.rnointerfermatrix.service.RnoInterferMatrixService;
import com.iscreate.rnointerfermatrix.task.JobExecutor;

@Component
public class ScheduledTasks {

	private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);

	@Autowired
	private RnoProperties rnoProperties;

	@Autowired
	private OracleMapper oracleMapper;

	@Autowired
	@Qualifier("rnoInterferMatrixServiceImpl")
	private RnoInterferMatrixService rnoInterferMatrixService;

	@Autowired
	@Qualifier("lteInterferMatrixTask")
	private JobExecutor jobExecutor;

	@Scheduled(fixedDelayString = "${scheduler.fixed-delay}")
	public void runParseTask() {
		JobProfile job = new JobProfile();
		boolean result = false;
		String jobId = null;
		try {
			String runMode = rnoProperties.getRunMode();
			if (runMode.equals("scheduler")) {
				logger.debug("定时器检查是否可领取新的干扰矩阵计算任务。");

				job.setJobStateStr(JobParseStatus.Waiting.toString());
				job.setJobType("CALC_LTE_INTERFER_MATRIX_NEW");
				jobId = oracleMapper.getJoBId(job);

				// 从数据库中取得将运行的数据解析导入任务
				if (jobId != null) {
					logger.debug("定时器领取到新的干扰矩阵计算任务。");
					job.setJobId(Long.parseLong(jobId));
					job.setJobStateStr(JobParseStatus.Running.toString());
					oracleMapper.updateJobRunningStatus(job);

					// 更新任务开始时间
					job.setLaunchTime(new Date());
					oracleMapper.updateJobBegTime(job);

					result = jobExecutor.runJobInternal(Long.parseLong(jobId));

					if (result) {
						job.setFinishTime(new Date());
						job.setJobStateStr(JobParseStatus.Succeded.toString());
						oracleMapper.updateJobEndTime(job);
					} else {
						job.setFinishTime(new Date());
						job.setJobStateStr(JobParseStatus.Fail.toString());
						oracleMapper.updateJobEndTime(job);
					}
				}
			}
		} catch (Exception e) {
			job.setFinishTime(new Date());
			job.setJobStateStr(JobParseStatus.Fail.toString());
			oracleMapper.updateJobEndTime(job);
			e.printStackTrace();
		}
	}
}
