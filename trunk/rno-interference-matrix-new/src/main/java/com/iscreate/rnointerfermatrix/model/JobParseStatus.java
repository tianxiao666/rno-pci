package com.iscreate.rnointerfermatrix.model;

public enum JobParseStatus {

	Waiting("Waiting"), Running("Running"), Fail("Fail"), Succeded("Succeded"), Stopping("Stopping");

	String desc;

	private JobParseStatus(String desc) {
		this.desc = desc;
	}

	@Override
	public String toString() {
		return this.desc;
	}
}
