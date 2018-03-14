package com.iscreate.rno.microservice.pci.afp.model;

import com.iscreate.rno.microservice.pci.afp.tool.DateUtil;

/**
 * 查询SF扫频测量描述新的查询条件
 * 
 * @author li.tf
 *
 */
public class G4SfDescQueryCond {
	private long cityId;
	private String factory;
	private String meaBegTime;
	private String meaEndTime;
	private final static String tableName = "RNO_4G_SF_DESC";

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
		return tableName.toUpperCase();
	}

	@Override
	public String toString() {
		return "G4SfDescQueryCond [cityId=" + cityId + ", factory=" + factory + ", meaBegTime=" + meaBegTime
				+ ", meaEndTime=" + meaEndTime + "]";
	}

}
