package com.iscreate.rno.microservice.pci.afp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.iscreate.rno.microservice.pci.afp.task.JobExecutor;

@Service
public class RnoJobServiceImpl implements RnoJobService {
	@Autowired
	@Qualifier("rnoNewPciPlanTask")
	private JobExecutor jobExecutor;
	@Override
	public boolean runJob(long jobId) {
		return jobExecutor.runJobInternal(jobId);
	}

}
