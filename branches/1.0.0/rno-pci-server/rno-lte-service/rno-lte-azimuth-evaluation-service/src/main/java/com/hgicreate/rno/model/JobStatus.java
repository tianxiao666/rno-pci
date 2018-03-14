package com.hgicreate.rno.model;

/**
 * 任务状态
 *
 * @author li.tf
 * @date 2018-01-12 14:08:33
 */

public enum JobStatus {

    /**
     * 任务状态
     */
    Waiting("Waiting"),
    Running("Running"),
    Fail("Fail"),
    Succeeded("Succeeded"),
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
