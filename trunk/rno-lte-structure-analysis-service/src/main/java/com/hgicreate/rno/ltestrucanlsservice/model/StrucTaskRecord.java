package com.hgicreate.rno.ltestrucanlsservice.model;

public class StrucTaskRecord {
	private long jobId;

	private String taskName;
	private String taskDesc;

	private long cityId;
	private String cityName;

	private String begMeaTime;
	private String endMeaTime;

	private String dlFileName;

    public long getJobId() {
        return jobId;
    }

    public void setJobId(long jobId) {
        this.jobId = jobId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskDesc() {
        return taskDesc;
    }

    public void setTaskDesc(String taskDesc) {
        this.taskDesc = taskDesc;
    }

    public long getCityId() {
        return cityId;
    }

    public void setCityId(long cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getBegMeaTime() {
        return begMeaTime;
    }

    public void setBegMeaTime(String begMeaTime) {
        this.begMeaTime = begMeaTime;
    }

    public String getEndMeaTime() {
        return endMeaTime;
    }

    public void setEndMeaTime(String endMeaTime) {
        this.endMeaTime = endMeaTime;
    }

    public String getDlFileName() {
        return dlFileName;
    }

    public void setDlFileName(String dlFileName) {
        this.dlFileName = dlFileName;
    }

    @Override
    public String toString() {
        return "StrucTaskRecord{" +
                "jobId=" + jobId +
                ", taskName='" + taskName + '\'' +
                ", taskDesc='" + taskDesc + '\'' +
                ", cityId=" + cityId +
                ", cityName='" + cityName + '\'' +
                ", begMeaTime='" + begMeaTime + '\'' +
                ", endMeaTime='" + endMeaTime + '\'' +
                ", dlFileName='" + dlFileName + '\'' +
                '}';
    }
}
