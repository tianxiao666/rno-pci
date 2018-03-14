package com.hgicreate.rno.ltestrucanlsservice.model;

public enum ReportStatus {

	Fail("失败"), Failall("全部失败"), Failpartly("部分失败"), Succeeded("全部成功"), Suc("成功");

	String desc;

	ReportStatus(String desc) {
		this.desc = desc;
	}

	@Override
	public String toString() {
		return this.desc;
	}
}
