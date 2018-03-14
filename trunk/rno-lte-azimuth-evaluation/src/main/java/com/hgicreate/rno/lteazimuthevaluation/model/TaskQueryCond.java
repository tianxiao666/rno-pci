package com.hgicreate.rno.lteazimuthevaluation.model;

/**
 * Created by chen.c10 on 2016/12/19.
 * 任务查询条件
 */
public class TaskQueryCond {
    private String isMine;
    private String account;
    private long cityId;
    private String taskName;
    private String taskStatus;
    private String meaTime;
    private String startSubmitTime;
    private String endSubmitTime;
    private int startIndex;
    private int endIndex;


    public String getIsMine() {
        return isMine;
    }

    public void setIsMine(String isMine) {
        this.isMine = isMine;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public long getCityId() {
        return cityId;
    }

    public void setCityId(long cityId) {
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

    public int getEndIndex() {
        return endIndex;
    }

    public void setEndIndex(int endIndex) {
        this.endIndex = endIndex;
    }

    @Override
    public String toString() {
        return "TaskQueryCond{" +
                "isMine='" + isMine + '\'' +
                ", account='" + account + '\'' +
                ", cityId=" + cityId +
                ", taskName='" + taskName + '\'' +
                ", taskStatus='" + taskStatus + '\'' +
                ", meaTime='" + meaTime + '\'' +
                ", startSubmitTime='" + startSubmitTime + '\'' +
                ", endSubmitTime='" + endSubmitTime + '\'' +
                ", startIndex=" + startIndex +
                ", endIndex=" + endIndex +
                '}';
    }
}
