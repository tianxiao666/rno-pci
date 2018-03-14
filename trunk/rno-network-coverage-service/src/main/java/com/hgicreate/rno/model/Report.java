package com.hgicreate.rno.model;

import java.io.Serializable;
import java.util.Date;

public class Report implements Serializable {

	private static final long serialVersionUID = 1L;

	private long jobId;

	private long reportId;

	private String stage = "";

	private Date begTime;

	private Date endTime;

	private String finishState = "";

	private String attMsg = "";

	private Integer reportType = 1;

	public Report() {
	}

	public Report(Report report) {
		if (report != null) {
			this.reportId = report.getReportId();
			this.attMsg = report.getAttMsg();
			this.begTime = report.getBegTime();
			this.endTime = report.getEndTime();
			this.finishState = report.getFinishState();
			this.stage = report.getStage();
			this.reportType = report.getReportType();
		}
	}

	public long getReportId() {
		return reportId;
	}

	public void setReportId(long reportId) {
		this.reportId = reportId;
	}

	public long getJobId() {
		return jobId;
	}

	public void setJobId(long jobId) {
		this.jobId = jobId;
	}

	public String getStage() {
		return stage;
	}

	public void setStage(String stage) {
		this.stage = stage;
	}

	public Date getBegTime() {
		return begTime;
	}

	public void setBegTime(Date begTime) {
		this.begTime = begTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getFinishState() {
		return finishState;
	}

	public void setFinishState(String finishState) {
		this.finishState = finishState;
	}

	public String getAttMsg() {
		return attMsg;
	}

	public void setAttMsg(String attMsg) {
		this.attMsg = attMsg;
	}

	public Integer getReportType() {
		return reportType;
	}

	public void setReportType(Integer reportType) {
		this.reportType = reportType;
	}

	public void setSystemFields(String stage, Date begTime, Date endTime, String finishState, String attMsg) {
		this.stage = stage;
		this.begTime = begTime;
		this.endTime = endTime;
		this.finishState = finishState;
		this.attMsg = attMsg;
		this.reportType = 2;// 系统类型
	}

	public void setFields(String stage, Date begTime, Date endTime, String finishState, String attMsg) {
		this.stage = stage;
		this.begTime = begTime;
		this.endTime = endTime;
		this.finishState = finishState;
		this.attMsg = attMsg;
		this.reportType = 1;// 业务类型
	}

	@Override
	public String toString() {
		return "Report [jobId=" + jobId + ", reportId=" + reportId + ", stage=" + stage + ", begTime=" + begTime
				+ ", endTime=" + endTime + ", finishState=" + finishState + ", attMsg=" + attMsg + ", reportType="
				+ reportType + "]";
	}

}
