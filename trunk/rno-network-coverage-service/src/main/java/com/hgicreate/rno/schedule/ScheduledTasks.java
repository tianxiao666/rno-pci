package com.hgicreate.rno.schedule;

import java.util.Date;

import com.hgicreate.rno.mapper.G4AzimuthCalcMapper;
import com.hgicreate.rno.model.JobParseStatus;
import com.hgicreate.rno.model.JobProfile;
import com.hgicreate.rno.properties.RnoProperties;
import com.hgicreate.rno.service.Rno4GAzimuthCalcService;
import com.hgicreate.rno.service.task.pci.Rno4GAzimuthCalcTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {

	private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);

	@Autowired
	private RnoProperties rnoProperties;

	@Autowired
	private G4AzimuthCalcMapper g4AzimuthCalcMapper;

	@Autowired
	@Qualifier("rno4GAzimuthCalcServiceImpl")
	private Rno4GAzimuthCalcService rno4GAzimuthCalcService;

	@Autowired
	private Rno4GAzimuthCalcTask rno4GAzimuthCalcTask;

	@Scheduled(fixedDelayString = "${scheduler.fixed-delay}")
	public void runParseTask() {
		JobProfile job = new JobProfile();
		boolean result = false;
		String jobId = null;
		try {
			String runMode = rnoProperties.getRunMode();
			if (runMode.equals("scheduler")) {
				logger.debug("定时器检查是否可领取新的4g方位角计算任务。");

				job.setJobStateStr(JobParseStatus.Waiting.toString());
				job.setJobType("RNO_4G_AZIMUTH_CALC");
				jobId = g4AzimuthCalcMapper.getJoBId(job);

				// 从数据库中取得将运行的数据解析导入任务
				if (jobId != null) {
					logger.debug("定时器领取到新的4g方位角计算任务。");
					job.setJobId(Long.parseLong(jobId));
					job.setJobStateStr(JobParseStatus.Running.toString());
					g4AzimuthCalcMapper.updateJobRunningStatus(job);

					// 更新任务开始时间
					job.setLaunchTime(new Date());
					g4AzimuthCalcMapper.updateJobBegTime(job);

					result = rno4GAzimuthCalcTask.runJobInternal(Long.parseLong(jobId));

					if (result) {
						job.setFinishTime(new Date());
						job.setJobStateStr(JobParseStatus.Succeded.toString());
						g4AzimuthCalcMapper.updateJobEndTime(job);
					} else {
						job.setFinishTime(new Date());
						job.setJobStateStr(JobParseStatus.Fail.toString());
						g4AzimuthCalcMapper.updateJobEndTime(job);
					}
				}
			}
		} catch (Exception e) {
			job.setFinishTime(new Date());
			job.setJobStateStr(JobParseStatus.Fail.toString());
			g4AzimuthCalcMapper.updateJobEndTime(job);
			e.printStackTrace();
		}
	}
}
