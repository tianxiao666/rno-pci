package com.iscreate.rno.microservice.pci.afp.model;

public enum JobParseStatus {

	Waiting("Waiting"), Running("Running"), Fail("Fail"), Succeded("Succeded");

	String desc;

	private JobParseStatus(String desc) {
		this.desc = desc;
	}

	@Override
	public String toString() {
		return this.desc;
	}
}
