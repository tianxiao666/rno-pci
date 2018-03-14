package com.hgicreate.rno.service.task.pci;

import com.hgicreate.rno.dao.Rno4GAzimuthCalcDao;
import com.hgicreate.rno.mapper.G4AzimuthCalcMapper;
import com.hgicreate.rno.model.DataParseStatus;
import com.hgicreate.rno.model.JobParseStatus;
import com.hgicreate.rno.model.JobProfile;
import com.hgicreate.rno.model.Report;
import com.hgicreate.rno.properties.RnoProperties;
import com.hgicreate.rno.tool.DateUtil;
import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Component
public strictfp class G4AzimuthCalcConfig {

	private static final Logger logger = LoggerFactory.getLogger(G4AzimuthCalcConfig.class);

	@SuppressWarnings("unused")
	private RnoProperties rnoProperties;
	@Autowired
	private G4AzimuthCalcMapper g4AzimuthCalcMapper;
	@Autowired
	private Rno4GAzimuthCalcDao rno4GAzimuthCalcDao;
	
	@Resource(name = "hadoopConfig")
	private Configuration conf;
	
//	private Configuration conf;
	

	private long jobId = -1;
	
	private String filePath = "";
	private String rdFileName = "";
	private String downFileName = "";
	private String redOutPath = ""; // reduce缺省输出目录
	private String startMeaDate = "";
	private String endMeaDate = "";
	private long startMeaMillis = 0;
	private long endMeaMillis = 0 ;
	private long cityId = 0;
	private String pciOriPath = "";
	// 保存返回信息
	private String returnInfo = "";

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getRdFileName() {
		return rdFileName;
	}

	public void setRdFileName(String rdFileName) {
		this.rdFileName = rdFileName;
	}

	public String getDownFileName() {
		return downFileName;
	}

	public void setDownFileName(String downFileName) {
		this.downFileName = downFileName;
	}

	public void setRedOutPath(String redOutPath) {
		this.redOutPath = redOutPath;
	}

	public String getStartMeaDate() {
		return startMeaDate;
	}

	public void setStartMeaDate(String startMeaDate) {
		this.startMeaDate = startMeaDate;
	}

	public String getEndMeaDate() {
		return endMeaDate;
	}

	public void setEndMeaDate(String endMeaDate) {
		this.endMeaDate = endMeaDate;
	}

	public long getCityId() {
		return cityId;
	}

	public void setCityId(long cityId) {
		this.cityId = cityId;
	}
	public String getPciOriPath() {
		return pciOriPath;
	}

	public void setPciOriPath(String pciOriPath) {
		this.pciOriPath = pciOriPath;
	}
	public G4AzimuthCalcConfig() {
		// TODO Auto-generated constructor stub
	}
	public G4AzimuthCalcConfig(long jobId, RnoProperties rnoProperties, G4AzimuthCalcMapper g4AzimuthCalcMapper, Rno4GAzimuthCalcDao rno4GAzimuthCalcDao) {
		this.jobId = jobId;
		this.rnoProperties = rnoProperties;
		this.g4AzimuthCalcMapper = g4AzimuthCalcMapper;
		this.rno4GAzimuthCalcDao = rno4GAzimuthCalcDao;
	}

	public Configuration getConf() {
		return conf;
	}

	public void setJobId(long jobId) {
		this.jobId = jobId;
	}

	public Long getJobId() {
		return jobId;
	}

	public String getFilePath() {
		return filePath;
	}

	public String getFileName() {
		return rdFileName;
	}


	public String getReturnInfo() {
		return returnInfo;
	}

	public void setReturnInfo(String returnInfo) {
		this.returnInfo = returnInfo;
	}

	public String getRedOutPath() {
		return redOutPath;
	}

	public boolean build4GAzimuthCalcConf() {

		logger.debug("jobId:{},g4AzimuthCalcMapper:{},rno4GAzimuthCalcDao:{}", jobId, g4AzimuthCalcMapper, rno4GAzimuthCalcDao);

		JobProfile job = new JobProfile(jobId);
		Date startTime = new Date();
		String msg = "";

		// 通过 jobId 获取4g方位角计算记录信息(rno_ms_4g_azimuth_job表），包括变小区的 CLOB 信息
		List<Map<String, Object>> rec = g4AzimuthCalcMapper.query4GAzimuthJobRecByJobId(jobId);

		logger.debug("rec:{}", rec);

		if (rec == null || rec.size() == 0) {
			msg = "任务配置信息不存在";
			logger.debug(msg);
			addReport(startTime, msg, DataParseStatus.Fail.toString(), "获取任务配置信息");
			updateOwnProgress("计算失败");
			updateJobEndTime(job, JobParseStatus.Fail.toString());
			return false;
		}

		Map<String, Object> g4AzimuthRec = rec.get(0);

		this.cityId = Long.parseLong(g4AzimuthRec.get("CITY_ID").toString());
		

		this.filePath = g4AzimuthRec.get("RESULT_DIR").toString();
		this.rdFileName = g4AzimuthRec.get("RD_FILE_NAME").toString();
		this.downFileName = g4AzimuthRec.get("DL_FILE_NAME").toString();
		this.startMeaDate = g4AzimuthRec.get("BEG_MEA_TIME").toString();
		this.endMeaDate = g4AzimuthRec.get("END_MEA_TIME").toString();
		this.startMeaDate = this.startMeaDate.split(" ")[0];
		this.endMeaDate = this.endMeaDate.split(" ")[0];
		/*conf.set("CALC_TYPE", "PCI");
		conf.setLong("jobId", jobId);
		conf.setLong("cityId", cityId);
		// 方案保存的文件路径
		conf.set("RESULT_DIR", filePath);
		conf.set("RD_FILE_NAME", rdFileName);
		conf.set("DOWN_FILE_NAME", downFileName);
		conf.set("DOWN_FILE_NAME", downFileName);
		
		*//*conf.setStrings("cellIdToAzimuth", azimuthArr);
		conf.setStrings("cellIdToBandType", bandtypeArr);*//*
		//分子RSRPtimes0 TIMESTOTAL
		conf.set("numerator", "RSRPTIMES0");*/
		this.pciOriPath=filePath+"/"+rdFileName;
		logger.info("pciOriPath   文件路径是===============>>>>>>>"+pciOriPath);
//		conf.set("writeFilePath", pciOriPath);
		return true;
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
		g4AzimuthCalcMapper.addReport(report);
	}

	/**
	 * 更新任务状态
	 */
	private void updateJobEndTime(JobProfile job, String jobStatus) {
		job.setJobStateStr(jobStatus);
		job.setFinishTime(new Date());
		g4AzimuthCalcMapper.updateJobEndTime(job);
	}
	public void updateOwnProgress(String jobStatus) {
		// 更新pci规划干扰计算表的进度
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("jobId", jobId);
		map.put("jobStatus", jobStatus);
		g4AzimuthCalcMapper.updatePciPlanWorkStatus(map);
	}
}
