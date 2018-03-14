package com.iscreate.rno.microservice.pci.afp.schedule;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.iscreate.rno.microservice.pci.afp.mapper.oracle.OracleMapper;
import com.iscreate.rno.microservice.pci.afp.model.JobParseStatus;
import com.iscreate.rno.microservice.pci.afp.model.JobProfile;
import com.iscreate.rno.microservice.pci.afp.properties.RnoProperties;
import com.iscreate.rno.microservice.pci.afp.service.RnoJobService;
import com.iscreate.rno.microservice.pci.afp.service.RnoPciAfpService;

@Component
public class ScheduledTasks {

	private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);

	@Autowired
	private RnoProperties rnoProperties;

	@Autowired
	private OracleMapper oracleMapper;

	@Autowired
	@Qualifier("rnoPciAfpServiceImpl")
	private RnoPciAfpService rnoPciAfpService;

	@Autowired
	@Qualifier("rnoJobServiceImpl")
	private RnoJobService rnoJobService;

	@Scheduled(fixedDelayString = "${scheduler.fixed-delay}")
	public void runParseTask() {
		JobProfile job = new JobProfile();
		boolean result = false;
		String jobId = null;
		try {
			String runMode = rnoProperties.getRunMode();
			if (runMode.equals("scheduler")) {
				logger.debug("定时器检查是否可领取新的PCI计算任务。");

				job.setJobStateStr(JobParseStatus.Waiting.toString());
				job.setJobType("RNO_PCI_PLAN_NEW");
				jobId = oracleMapper.getJoBId(job);

				// 从数据库中取得将运行的数据解析导入任务
				if (jobId != null) {
					logger.debug("定时器领取到新的PCI计算任务。");
					job.setJobId(Long.parseLong(jobId));
					job.setJobStateStr(JobParseStatus.Running.toString());
					oracleMapper.updateJobRunningStatus(job);

					// 更新任务开始时间
					job.setLaunchTime(new Date());
					oracleMapper.updateJobBegTime(job);

					result = rnoJobService.runJob(Long.parseLong(jobId));

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
