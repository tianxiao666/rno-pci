package com.hgicreate.rno.ltestrucanlsclient.model;

import java.io.Serializable;
import java.util.Date;

public class JobProfile implements Serializable {

	private static final long serialVersionUID = 1L;

	private long jobId;

	private Date submitTime = null;

	private Date launchTime = null;

	private Date finishTime = null;

	private Integer priority = 1;

	private String jobType;

	private String account;

	private String jobName;

	private String jobStateStr;

	private String description;

	private String status = "N";

	public JobProfile() {
	}

	public JobProfile(Long jobId) {
		this.jobId = jobId;
	}

	public Long getJobId() {
		return jobId;
	}

	public void setJobId(Long jobId) {
		this.jobId = jobId;
	}

	public Date getSubmitTime() {
		return submitTime;
	}

	public void setSubmitTime(Date submitTime) {
		this.submitTime = submitTime;
	}

	public Date getLaunchTime() {
		return launchTime;
	}

	public void setLaunchTime(Date launchTime) {
		this.launchTime = launchTime;
	}

	public Date getFinishTime() {
		return finishTime;
	}

	public void setFinishTime(Date finishTime) {
		this.finishTime = finishTime;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public String getJobType() {
		return jobType;
	}

	public void setJobType(String jobType) {
		this.jobType = jobType;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getJobStateStr() {
		return jobStateStr;
	}

	public void setJobStateStr(String jobStateStr) {
		this.jobStateStr = jobStateStr;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "JobProfile [jobId=" + jobId + ", submitTime=" + submitTime + ", launchTime=" + launchTime
				+ ", finishTime=" + finishTime + ", priority=" + priority + ", jobType=" + jobType + ", account="
				+ account + ", jobName=" + jobName + ", jobStateStr=" + jobStateStr + ", description=" + description
				+ ", status=" + status + "]";
	}

}
