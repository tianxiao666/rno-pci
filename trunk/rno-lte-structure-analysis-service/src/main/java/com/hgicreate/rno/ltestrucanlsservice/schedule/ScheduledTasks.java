package com.hgicreate.rno.ltestrucanlsservice.schedule;

import com.hgicreate.rno.ltestrucanlsservice.model.JobProfile;
import com.hgicreate.rno.ltestrucanlsservice.model.JobStatus;
import com.hgicreate.rno.ltestrucanlsservice.properties.RnoProperties;
import com.hgicreate.rno.ltestrucanlsservice.service.RnoCommonService;
import com.hgicreate.rno.ltestrucanlsservice.task.RnoLteStrucAnlsTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {

	private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);

	private RnoProperties rnoProperties;

	private RnoCommonService rnoCommonService;

	private RnoLteStrucAnlsTask rnoLteStrucAnlsTask;

	public ScheduledTasks(RnoProperties rnoProperties, RnoCommonService rnoCommonService, RnoLteStrucAnlsTask rnoLteStrucAnlsTask) {
		this.rnoProperties = rnoProperties;
		this.rnoCommonService = rnoCommonService;
		this.rnoLteStrucAnlsTask = rnoLteStrucAnlsTask;
	}

	@Scheduled(fixedDelayString = "${rno.scheduler.fixed-delay}")
	public void runParseTask() {
		String runMode = rnoProperties.getRunMode();
		if (runMode.equals("scheduler")) {
			logger.debug("定时器检查是否可领取新的Lte方位角计算任务。");
			try {
				JobProfile job = new JobProfile();
				job.setJobStateStr(JobStatus.Waiting.toString());
				job.setJobType("RNO_LTE_STRUC_ANLS");

				Long jobId = rnoCommonService.getOneJob(job);

				// 从数据库中取得将运行的数据解析导入任务
				if (jobId != null && jobId > 0) {
					logger.debug("定时器领取到新的Lte方位角计算任务。");
					rnoLteStrucAnlsTask.runJobInternal(jobId);
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("定时器检查并运行定时失败！");
			}
		}
	}
}
