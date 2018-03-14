package com.iscreate.rno.microservice.pci.afp.model;

import java.io.Serializable;
import java.util.Date;

public class RnoDataCollectRec implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long dataCollectId;

	private Date uploadTime;

	private Date businessTime;

	private String fileName;

	private String oriFileName;

	private String account;

	private Long cityId;

	private Integer businessDataType;

	private Long fileSize;

	private String fullPath;

	private String fileStatus;

	private Long jobId;

	private Date launchTime;

	private Date completeTime;

	public Long getDataCollectId() {
		return dataCollectId;
	}

	public void setDataCollectId(Long dataCollectId) {
		this.dataCollectId = dataCollectId;
	}

	public Date getUploadTime() {
		return uploadTime;
	}

	public void setUploadTime(Date uploadTime) {
		this.uploadTime = uploadTime;
	}

	public Date getBusinessTime() {
		return businessTime;
	}

	public void setBusinessTime(Date businessTime) {
		this.businessTime = businessTime;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getOriFileName() {
		return oriFileName;
	}

	public void setOriFileName(String oriFileName) {
		this.oriFileName = oriFileName;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public Long getCityId() {
		return cityId;
	}

	public void setCityId(Long cityId) {
		this.cityId = cityId;
	}

	public Integer getBusinessDataType() {
		return businessDataType;
	}

	public void setBusinessDataType(Integer businessDataType) {
		this.businessDataType = businessDataType;
	}

	public Long getFileSize() {
		return fileSize;
	}

	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
	}

	public String getFullPath() {
		return fullPath;
	}

	public void setFullPath(String fullPath) {
		this.fullPath = fullPath;
	}

	public String getFileStatus() {
		return fileStatus;
	}

	public void setFileStatus(String fileStatus) {
		this.fileStatus = fileStatus;
	}

	public Long getJobId() {
		return jobId;
	}

	public void setJobId(Long jobId) {
		this.jobId = jobId;
	}

	public Date getLaunchTime() {
		return launchTime;
	}

	public void setLaunchTime(Date launchTime) {
		this.launchTime = launchTime;
	}

	public Date getCompleteTime() {
		return completeTime;
	}

	public void setCompleteTime(Date completeTime) {
		this.completeTime = completeTime;
	}

	public RnoDataCollectRec(RnoDataCollectRec oldOne) {
		this.dataCollectId = oldOne.dataCollectId;
		this.uploadTime = oldOne.uploadTime;
		this.businessTime = oldOne.businessTime;
		this.fileName = oldOne.fileName;
		this.oriFileName = oldOne.oriFileName;
		this.account = oldOne.account;
		this.cityId = oldOne.cityId;
		this.businessDataType = oldOne.businessDataType;
		this.fileSize = oldOne.fileSize;
		this.fullPath = oldOne.fullPath;
		this.fileStatus = oldOne.fileStatus;
		this.jobId = oldOne.jobId;
		this.launchTime = oldOne.launchTime;
		this.completeTime = oldOne.completeTime;
	}

	public RnoDataCollectRec(Long dataCollectId, Date uploadTime, Date businessTime, String fileName,
			String oriFileName, String account, Long cityId, Integer businessDataType, Long fileSize, String fullPath,
			String fileStatus, Long jobId, Date launchTime, Date completeTime) {
		super();
		this.dataCollectId = dataCollectId;
		this.uploadTime = uploadTime;
		this.businessTime = businessTime;
		this.fileName = fileName;
		this.oriFileName = oriFileName;
		this.account = account;
		this.cityId = cityId;
		this.businessDataType = businessDataType;
		this.fileSize = fileSize;
		this.fullPath = fullPath;
		this.fileStatus = fileStatus;
		this.jobId = jobId;
		this.launchTime = launchTime;
		this.completeTime = completeTime;
	}

	public RnoDataCollectRec() {
		super();
	}

	@Override
	public String toString() {
		return "RnoDataCollectRec [dataCollectId=" + dataCollectId + ", uploadTime=" + uploadTime + ", businessTime="
				+ businessTime + ", fileName=" + fileName + ", oriFileName=" + oriFileName + ", account=" + account
				+ ", cityId=" + cityId + ", businessDataType=" + businessDataType + ", fileSize=" + fileSize
				+ ", fullPath=" + fullPath + ", fileStatus=" + fileStatus + ", jobId=" + jobId + ", launchTime="
				+ launchTime + ", completeTime=" + completeTime + "]";
	}

}
