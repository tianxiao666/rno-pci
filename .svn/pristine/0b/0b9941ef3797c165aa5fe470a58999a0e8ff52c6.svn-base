package com.iscreate.rno.microservice.pci.afp.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.iscreate.rno.microservice.pci.afp.dao.RnoPciAfpDao;
import com.iscreate.rno.microservice.pci.afp.mapper.oracle.OracleMapper;
import com.iscreate.rno.microservice.pci.afp.mapper.spark.SparkMapper;

@Component
public class RnoNewPciPlanTask implements JobExecutor {

	@Autowired
	private OracleMapper oracleMapper;
	
	@Autowired
	private SparkMapper sparkMapper;

	@Autowired
	@Qualifier("rnoPciAfpDaoImpl")
	private RnoPciAfpDao rnoPciAfpDao;

	public boolean runJobInternal(long jobId) {
		NewPciJob job = new NewPciJob(jobId, oracleMapper, sparkMapper, rnoPciAfpDao);
		boolean result = job.run();
		job.clean();
		return result;
	}
}
