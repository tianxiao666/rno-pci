package com.hgicreate.rno.ltestrucanlsservice.model;

public enum TaskState {

	Starting("开始分析"), Fail("计算失败"), Failall("全部失败"), Failpartly("部分失败"), Succeeded("计算成功");

	String desc;

	TaskState(String desc) {
		this.desc = desc;
	}

	@Override
	public String toString() {
		return this.desc;
	}
}
