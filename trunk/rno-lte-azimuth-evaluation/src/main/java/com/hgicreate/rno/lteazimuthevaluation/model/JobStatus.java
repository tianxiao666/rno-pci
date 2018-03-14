package com.hgicreate.rno.lteazimuthevaluation.model;

public enum JobStatus {

	Waiting("Waiting"), Running("Running"), Fail("Fail"), Succeeded("Succeeded"), Stopped("Stopped");

	String desc;

	JobStatus(String desc) {
		this.desc = desc;
	}

	@Override
	public String toString() {
		return this.desc;
	}
}
