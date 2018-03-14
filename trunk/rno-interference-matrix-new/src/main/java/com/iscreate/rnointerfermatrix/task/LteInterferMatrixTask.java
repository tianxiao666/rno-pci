package com.iscreate.rnointerfermatrix.task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.iscreate.rnointerfermatrix.dao.RnoInterferMatrixDao;
import com.iscreate.rnointerfermatrix.mapper.oracle.OracleMapper;
import com.iscreate.rnointerfermatrix.mapper.spark.SparkMapper;
import com.iscreate.rnointerfermatrix.model.DataParseStatus;
import com.iscreate.rnointerfermatrix.model.JobParseStatus;
import com.iscreate.rnointerfermatrix.model.JobProfile;
import com.iscreate.rnointerfermatrix.model.Report;

@Component
public class LteInterferMatrixTask implements JobExecutor {
	private static final Logger log = LoggerFactory.getLogger(LteInterferMatrixTask.class);

	@Autowired
	private OracleMapper oracleMapper;
	
	@Autowired
	private SparkMapper sparkMapper;

	@Autowired
	@Qualifier("rnoInterferMatrixDaoImpl")
	private RnoInterferMatrixDao rnoInterferMatrixDao;

	private long matrixRecId = -1;

	private long jobId;
	
	private boolean isUseSf = false;
	
	private List<String> fileNameList = new ArrayList<String>();
	
	@Value("${samefreqcellcoefweight}")
	private double samefreqcellcoefweight;
	
	@Value("${switchratioweight}")
	private double switchratioweight;
	
	@Value("${dislimit}")
	private double dislimit;
	
	@Value("${rsrp0minus1weight}")
	private double rsrp0minus1weight;
	
	@Value("${rsrp1weight}")
	private double rsrp1weight;
	
	@Value("${minmeasuresum}")
	private long minmeasuresum;
	
	@Value("${mincorrelation}")
	private double mincorrelation;
	

	public boolean runJobInternal(long thisJobId) {

		jobId = thisJobId;

		log.info("pci优化干扰矩阵任务开始");

		Date startTime = new Date();
		JobProfile job = new JobProfile(jobId);
		String msg = "";
		boolean flag = false;

		// 通过jobId获取干扰矩阵计算记录信息
		List<Map<String, Object>> pciPlanRec = oracleMapper.query4GInterferMatrixRecByJobId(jobId);
		if (pciPlanRec.size() <= 0) {
			updateJobEndTime(job, JobParseStatus.Fail.toString());
			return false;
		}
		matrixRecId = Long.parseLong(pciPlanRec.get(0).get("MARTIX_DESC_ID").toString());

		//
		String startMeaDate = pciPlanRec.get(0).get("START_MEA_DATE").toString();
		String endMeaDate = pciPlanRec.get(0).get("END_MEA_DATE").toString();
		int areaId = Integer.parseInt(pciPlanRec.get(0).get("CITY_ID").toString());

		if (pciPlanRec.get(0).get("SAMEFREQCELLCOEFWEIGHT") != null
				&& pciPlanRec.get(0).get("SWITCHRATIOWEIGHT") != null) {
			samefreqcellcoefweight = Double.parseDouble(pciPlanRec.get(0).get("SAMEFREQCELLCOEFWEIGHT").toString());
			switchratioweight = Double.parseDouble(pciPlanRec.get(0).get("SWITCHRATIOWEIGHT").toString());
		}
		
		fileNameList = Arrays.asList(null == pciPlanRec.get(0).get("SF_FILES") ? new String[0]
				: pciPlanRec.get(0).get("SF_FILES").toString().trim().split(","));

		if (!fileNameList.isEmpty() && !fileNameList.get(0).isEmpty()) {
			isUseSf = true;
		}

		/************ PCI干扰计算 start ************/
		log.debug("准备pci优化干扰矩阵的计算任务...");
		
		// 更新pci优化干扰矩阵状态
		updateOwnProgress("正在计算");
		
		List<String> list = null;
		Map<String, Object> timeMap = new HashMap<>();
		Map<String, Object> dataMap = new HashMap<>();

		try {
	
			timeMap.put("startMeaDay", startMeaDate);
			timeMap.put("endMeaDay", endMeaDate);
			timeMap.put("areaId", areaId);
			dataMap.put("jobId", jobId);
			dataMap.put("areaId", areaId);
			
			//准备MR临时表数据
			list =  sparkMapper.queryMrMeaDate(timeMap);
			dataMap.put("meaTime", list);
			if(!list.isEmpty()){
				sparkMapper.createMrTempTable(dataMap);
			}
		    
		    //准备HO临时表数据
			list =  sparkMapper.queryHoMeaDate(timeMap);
			dataMap.put("meaTime", list);
			if(!list.isEmpty()){
				sparkMapper.createHoTempTable(dataMap);
			}
		    
		    //准备SF临时表数据
			if (isUseSf) {
				list = sparkMapper.querySfMeaDate(timeMap);
				dataMap.put("meaTime", list);
				if (!list.isEmpty()) {
					sparkMapper.createSfTempTable(dataMap);
				}
			}
			
		    //计算干扰矩阵并入到仓库
			Map<String, Object> map = new HashMap<>();
			map.put("jobId", jobId);
			map.put("samefreqcellcoefweight", samefreqcellcoefweight);
			map.put("switchratioweight", switchratioweight);
			map.put("dislimit", dislimit);
			map.put("rsrp0minus1weight", rsrp0minus1weight);
			map.put("rsrp1weight", rsrp1weight);
			map.put("minmeasuresum", minmeasuresum);
			map.put("mincorrelation", mincorrelation);
			map.put("startMeaDay", startMeaDate);
			map.put("endMeaDay", endMeaDate);
			map.put("areaId", areaId);
			if(isUseSf) {
				sparkMapper.createMatrix(map);
			} else {
				sparkMapper.createMatrixWithoutSf(map);		
			}
			
			flag = true;
					
		} catch (Exception e) {
			e.printStackTrace();
			flag = false;
		}
		/************ pci优化干扰矩阵，并保存结果文件 end ************/
		log.info("pci优化干扰矩阵任务结束");
		if (flag) {
			msg = job.getJobName() + ",结果：任务成功！";
			log.info(msg);
			addReport(startTime, msg, DataParseStatus.Suc.toString(), "计算总结");
			updateJobEndTime(job, JobParseStatus.Succeded.toString());
			updateOwnProgress("计算完成");
			return true;
		} else {
			msg = job.getJobName() + ",结果：任务失败！";
			log.info(msg);
			addReport(startTime, msg, DataParseStatus.Fail.toString(), "计算总结");
			updateJobEndTime(job, JobParseStatus.Fail.toString());
			updateOwnProgress("计算失败");
			return false;
		}
	}

	private void updateOwnProgress(String jobStatus) {
		// 更新pci优化干扰矩阵表的进度
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("matrixRecId", matrixRecId);
		map.put("jobStatus", jobStatus);
		oracleMapper.update4GInterMatrixWorkStatus(map);
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

}
