package com.iscreate.rno.microservice.pci.afp.model;

public class TaskCond {
	private String account;
	private String isMine;
	private int cityId;
	private String taskName;
	private String taskStatus;
	private String meaTime;
	private String startSubmitTime;
	private String endSubmitTime;
	private int startIndex;
	private int cnt;

	@Override
	public String toString() {
		return "TaskCond [account=" + account + ", isMine=" + isMine + ", cityId=" + cityId + ", taskName=" + taskName
				+ ", taskStatus=" + taskStatus + ", meaTime=" + meaTime + ", startSubmitTime=" + startSubmitTime
				+ ", endSubmitTime=" + endSubmitTime + ", startIndex=" + startIndex + ", cnt=" + cnt + "]";
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getIsMine() {
		return isMine;
	}

	public void setIsMine(String isMine) {
		this.isMine = isMine;
	}

	public int getCityId() {
		return cityId;
	}

	public void setCityId(int cityId) {
		this.cityId = cityId;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getTaskStatus() {
		return taskStatus;
	}

	public void setTaskStatus(String taskStatus) {
		this.taskStatus = taskStatus;
	}

	public String getMeaTime() {
		return meaTime;
	}

	public void setMeaTime(String meaTime) {
		this.meaTime = meaTime;
	}

	public String getStartSubmitTime() {
		return startSubmitTime;
	}

	public void setStartSubmitTime(String startSubmitTime) {
		this.startSubmitTime = startSubmitTime;
	}

	public String getEndSubmitTime() {
		return endSubmitTime;
	}

	public void setEndSubmitTime(String endSubmitTime) {
		this.endSubmitTime = endSubmitTime;
	}

	public int getStartIndex() {
		return startIndex;
	}

	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}

	public int getCnt() {
		return cnt;
	}

	public void setCnt(int cnt) {
		this.cnt = cnt;
	}
}
