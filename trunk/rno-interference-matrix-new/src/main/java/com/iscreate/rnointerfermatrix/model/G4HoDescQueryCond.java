package com.iscreate.rnointerfermatrix.model;

import com.iscreate.rnointerfermatrix.tool.DateUtil;

/**
 * 查询HO测量描述新的查询条件
 * 
 * @author li.tf
 *
 */
public class G4HoDescQueryCond {
	private long cityId;
	private String factory;
	private String meaBegTime;
	private String meaEndTime;

	public long getCityId() {
		return cityId;
	}

	public void setCityId(long cityId) {
		this.cityId = cityId;
	}

	public String getMeaBegTime() {
		return meaBegTime;
	}

	public void setMeaBegTime(String meaBegTime) {
		this.meaBegTime = meaBegTime;
	}

	public String getMeaEndTime() {
		return meaEndTime;
	}

	public void setMeaEndTime(String meaEndTime) {
		this.meaEndTime = meaEndTime;
	}

	public String getFactory() {
		return factory;
	}

	public void setFactory(String factory) {
		this.factory = factory;
	}

	public String buildStartRow() {
		DateUtil dateUtil = new DateUtil();
		long sMill = dateUtil.parseDateArbitrary(this.meaBegTime).getTime();
		String startRow = cityId + "_" + sMill + "_#";
		return startRow;
	}

	public String buildStopRow() {
		DateUtil dateUtil = new DateUtil();
		long eMill = dateUtil.parseDateArbitrary(this.meaEndTime).getTime();
		String stopRow = cityId + "_" + eMill + "_~";
		return stopRow;
	}

	public String buildTable() {

		String table = "RNO_4G_HO_DESC";
		return table;
	}
}