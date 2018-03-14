package com.hgicreate.rno.lteazimuthevaluation.model;

public class AzimuthJobRecord {

	private long jobId;

	private String taskName;
	private String taskDesc;

	private long provinceId;
    private String provinceName;
	private long cityId;
	private String cityName;

	private String begMeaTime;
	private String endMeaTime;

	private String dlFileName;

	private String createTime;
	private String modTime;

	private String finishState;

	private String evalType;

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

    public long getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(long provinceId) {
        this.provinceId = provinceId;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
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

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getModTime() {
        return modTime;
    }

    public void setModTime(String modTime) {
        this.modTime = modTime;
    }

    public String getFinishState() {
        return finishState;
    }

    public void setFinishState(String finishState) {
        this.finishState = finishState;
    }

    public String getEvalType() {
        return evalType;
    }

    public void setEvalType(String evalType) {
        this.evalType = evalType;
    }

    @Override
    public String toString() {
        return "AzimuthJobRecord{" +
                "jobId=" + jobId +
                ", taskName='" + taskName + '\'' +
                ", taskDesc='" + taskDesc + '\'' +
                ", provinceId=" + provinceId +
                ", provinceName='" + provinceName + '\'' +
                ", cityId=" + cityId +
                ", cityName='" + cityName + '\'' +
                ", begMeaTime='" + begMeaTime + '\'' +
                ", endMeaTime='" + endMeaTime + '\'' +
                ", dlFileName='" + dlFileName + '\'' +
                ", createTime='" + createTime + '\'' +
                ", modTime='" + modTime + '\'' +
                ", finishState='" + finishState + '\'' +
                ", evalType='" + evalType + '\'' +
                '}';
    }
}
