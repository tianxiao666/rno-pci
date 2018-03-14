package com.hgicreate.rno.service.task.pci;

import com.hgicreate.rno.dao.Rno4GAzimuthCalcDao;
import com.hgicreate.rno.mapper.G4AzimuthCalcMapper;
import com.hgicreate.rno.model.DataParseStatus;
import com.hgicreate.rno.model.JobParseStatus;
import com.hgicreate.rno.model.JobProfile;
import com.hgicreate.rno.model.Report;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class Rno4GAzimuthCalcTask {

	private static final Logger logger = LoggerFactory.getLogger(Rno4GAzimuthCalcTask.class);
	private long jobId;

	@Autowired
	private G4AzimuthCalcMapper g4AzimuthCalcMapper;

	@Autowired
	@Qualifier("rno4GAzimuthCalcDaoImpl")
	private Rno4GAzimuthCalcDao rno4GAzimuthCalcDao;
	@Autowired
	private G4AzimuthCalcConfig config;
	/*
     * spark
     */
	@Value("${spring.datasource.spark.url}")
	private String sparkUrl;
	@Value("${spring.datasource.spark.username}")
	private String sparkUser;
	@Value("${spring.datasource.spark.password}")
	private String sparkPassword;
	@Value("${spring.datasource.spark.driverClassName}")
	private String sparkDriveName;
	public boolean runJobInternal(long thisJobId) {
		jobId = thisJobId;
		JobProfile job = g4AzimuthCalcMapper.getJobNameByJobId(jobId);
		String msg = "";
		Date startTime = new Date();
		config.setJobId(jobId);
		boolean canCalc = config.build4GAzimuthCalcConf();
		if (!canCalc) {
			/*msg = "+++>>>名称=" + job.getJobName() + ",结果：任务失败！原因：初始化失败。";
			logger.error(msg);
			// 保存报告信息
			addReport(startTime, msg, DataParseStatus.Fail.toString(), "任务初始化");
			updateOwnProgress("计算失败");
			updateJobEndTime(job, JobParseStatus.Fail.toString());*/
			return false;
		}
		updateOwnProgress("正在计算");
		Configuration conf = config.getConf();
	    /* 提交人 */
		System.setProperty("HADOOP_USER_NAME", conf.get("hadoop.user.name"));
		Path path = new Path(conf.get("RESULT_DIR")+"/out");
		try {
			// 先在 HDFS 中删除已经存在的文件，因为 HDFS 文件系统是不能修改的。
			FileSystem fs = FileSystem.get(conf);
			fs.delete(path, true);
		} catch (IOException e1) {
			e1.printStackTrace();
			logger.error("设置用于 4g azimuth计算job的输出路径失败！");
			addReport(startTime, msg, DataParseStatus.Fail.toString(), "设置 4g azimuth计算job输出路径");
			updateOwnProgress("计算失败");
			updateJobEndTime(job, JobParseStatus.Fail.toString());
			return false;
		}
		DataSource db = DataSourceBuilder.create().url(sparkUrl).username(sparkUser).password(sparkPassword)
				.driverClassName(sparkDriveName).build();
		Connection connection = null;
		try {
			connection = db.getConnection();
		}catch (Exception e){
			logger.error("准备SPARK数据仓库连接失败！");
			addReport(startTime, msg, DataParseStatus.Fail.toString(), "准备SPARK数据仓库连接失败");
			updateOwnProgress("计算失败");
			updateJobEndTime(job, JobParseStatus.Fail.toString());
			e.printStackTrace();
			return false;
		}
		//从HIVE数据仓库规整数据
		List<Map<String,Object>>  mrDatas=rno4GAzimuthCalcDao.queryNetWorkCoverDataFromSparkMrTable(config.getCityId(),config.getStartMeaDate(),config.getEndMeaDate(),connection,jobId);
		if (mrDatas==null || mrDatas.size()==0) {
			msg = "+++>>>名称=" + job.getJobName() + ",结果：任务失败！原因：该时间区的MR数据资源不存在，请选择合适的时间段。";
			logger.error(msg);
			// 保存报告信息
			addReport(startTime, msg, DataParseStatus.Fail.toString(), "该时间区的MR数据资源不存在，请选择合适的时间段！");
			updateOwnProgress("计算失败");
			updateJobEndTime(job, JobParseStatus.Fail.toString());
			return false;
		}
//		System.out.println("--------------------------"+mrDatas.get(0).get("cell_name").toString());
//		bad SQL grammar []; nested exception is java.sql.SQLSyntaxErrorException: ORA-01745: 无效的主机/绑定变量名
//		boolean flag = rno4GAzimuthCalcDao.addNetworkCoverResBatch(mrDatas);
		try {
			//向oracle数据库录入规整数据资源
			//每批处理1000个记录
			int batchCount = 1000;
			//每批最后一个的下标
			int batchLastIndex = batchCount;
			for (int index = 0; index < mrDatas.size();) {
				if (batchCount>=mrDatas.size()){
					rno4GAzimuthCalcDao.addNetworkCoverResBatch(mrDatas);
					//数据插入完毕，退出批处理循环
					break;
				}else{
					//设置下一批下标
					if (mrDatas.size()-index<=1000){
//						System.out.println("mrDatas.subList(index,mrDatas.size())==="+mrDatas.subList(index,mrDatas.size()));
						rno4GAzimuthCalcDao.addNetworkCoverResBatch(mrDatas.subList(index,mrDatas.size()));
						break;
					}else{
						rno4GAzimuthCalcDao.addNetworkCoverResBatch(mrDatas.subList(index,batchLastIndex));
					}
//					System.out.println("index:" + index+ " batchLastIndex:" + batchLastIndex);
					index = batchLastIndex;
					batchLastIndex = index + (batchCount - 1);
				}
			}
			logger.info("网络覆盖  4g azimuth 计算完成");
			msg = "任务名称=" + job.getJobName() + ",结果：任务成功！";
			addReport(startTime, msg, DataParseStatus.Succeded.toString(), "保存网络覆盖分析数据任务");
			updateOwnProgress("计算完成");
			updateJobEndTime(job, JobParseStatus.Succeded.toString());
		}catch (Exception e){
			msg = "+++>>>任务名称=" + job.getJobName() + ",结果：任务失败！原因：保存网络覆盖分析数据失败。";
			logger.error(msg);
			// 保存报告信息
			addReport(startTime, msg, DataParseStatus.Fail.toString(), "保存网络覆盖分析数据任务");
			updateOwnProgress("计算失败");
			updateJobEndTime(job, JobParseStatus.Fail.toString());
			return false;
		}


			//save4GAzimuthInHdfs(conf,config.getPciOriPath(),mrDatas);
			/*if (flag) {
				logger.info("网络覆盖  4g azimuth 计算完成");
				msg = "任务名称=" + job.getJobName() + ",结果：任务成功！";
				addReport(startTime, msg, DataParseStatus.Succeded.toString(), "保存网络覆盖分析数据任务");
				updateOwnProgress("计算完成");
				updateJobEndTime(job, JobParseStatus.Succeded.toString());
			}else {
				msg = "+++>>>任务名称=" + job.getJobName() + ",结果：任务失败！原因：保存网络覆盖分析数据失败。";
				logger.error(msg);
				// 保存报告信息
				addReport(startTime, msg, DataParseStatus.Fail.toString(), "保存网络覆盖分析数据任务");
				updateOwnProgress("计算失败");
				updateJobEndTime(job, JobParseStatus.Fail.toString());
				return false;
			}*/
			return true;
	}

	public void updateOwnProgress(String jobStatus) {
		// 更新pci规划干扰计算表的进度
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("jobId", jobId);
		map.put("jobStatus", jobStatus);
		g4AzimuthCalcMapper.updatePciPlanWorkStatus(map);
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
	public void save4GAzimuthInHdfs(Configuration conf,String filePath, List<Map<String,Object>>  mrDatas) {
		String realFilePath = filePath;

		FileSystem fs = null;
		try {
			fs = FileSystem.get(conf);
		} catch (IOException e1) {
			System.err.println("save4GAzimuthInHdfs过程：打开hdfs文件系统出错！");
			e1.printStackTrace();
		}
		//先删除原有文件
		Path oldFilePath = new Path(URI.create(realFilePath));
		try {
			if(fs.exists(oldFilePath)) {
				fs.delete(oldFilePath, false);
			}
		} catch (IOException e2) {
			System.err.println("save4GAzimuthInHdfs过程：保存文件时，删除原有文件出错！");
			e2.printStackTrace();
		}
		//创建新文件
		Path filePathObj = new Path(URI.create(realFilePath));
		//创建流
		OutputStream dataOs= null;
		try {
			dataOs = fs.create(filePathObj, true);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		BufferedOutputStream bo=null ;
		String line="";
		int cellId=0;
		int  diffAzimuth=0;
		int curAzimuth=0;
		int calAzimuth=0;
		String cellName="";

		try {

			bo = new BufferedOutputStream(dataOs);

			for (int i = 0; i < mrDatas.size(); i++) {
				cellName = mrDatas.get(i).get("cell_name").toString();
				cellId = Integer.parseInt(mrDatas.get(i).get("cell_name").toString());
				curAzimuth = Integer.parseInt(mrDatas.get(i).get("cur_azimuth").toString());
				calAzimuth = Integer.parseInt(mrDatas.get(i).get("cal_azimuth").toString());
				diffAzimuth = Integer.parseInt(mrDatas.get(i).get("diff_azimuth").toString());
				line=cellName+"#"+cellId+"#"+curAzimuth+"#"+calAzimuth+"#"+diffAzimuth;
				bo.write(Bytes.toBytes(line+"\n"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				bo.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
