package com.hgicreate.rno.lte.common.model;

/**
 * @author chen.c10
 */

public enum JobStatus {
    // 等待开始
    Waiting("Waiting"),
    // 正在运行
    Running("Running"),
    // 任务失败
    Fail("Fail"),
    // 任务成功
    Succeeded("Succeeded"),
    // 任务结束
    Stopped("Stopped");

    String desc;

    JobStatus(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return this.desc;
    }
}
