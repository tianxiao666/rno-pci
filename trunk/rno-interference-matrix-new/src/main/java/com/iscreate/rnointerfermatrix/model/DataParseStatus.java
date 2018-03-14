package com.iscreate.rnointerfermatrix.model;

public enum DataParseStatus {

	Waiting("等待解析"), Parsing("正在解析"), Fail("失败"), Failall("全部失败"), Failpartly("部分失败"), Succeded("全部成功"), Suc("成功");

	String desc;

	private DataParseStatus(String desc) {
		this.desc = desc;
	}

	@Override
	public String toString() {
		return this.desc;
	}
}
