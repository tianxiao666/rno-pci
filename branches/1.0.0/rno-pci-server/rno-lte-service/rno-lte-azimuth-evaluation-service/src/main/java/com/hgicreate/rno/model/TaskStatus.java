package com.hgicreate.rno.model;

/**
 * 任务状态
 *
 * @author li.tf
 * @date 2018-01-12 14:13:13
 */

public enum TaskStatus {

    /**
     * 任务状态
     */
    Fail("失败"),
    Succeeded("全部成功");

    String desc;

    TaskStatus(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return this.desc;
    }
}
