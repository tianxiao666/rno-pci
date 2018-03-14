package com.iscreate.rnointerfermatrix.model;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 查询MR测量描述新的查询条件
 * 
 * @author chao.xj
 *
 */
public class G4MrDescQueryCond {

	private static final Logger logger = LoggerFactory.getLogger(G4MrDescQueryCond.class);

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
		logger.debug("this.meaBegTime = {}", this.meaBegTime);
		LocalDateTime dateTime = LocalDateTime.parse(meaBegTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		long sMill = dateTime.toEpochSecond(ZoneOffset.ofHours((8))) * 1000;
		String startRow = cityId + "_" + sMill + "_#";
		return startRow;
	}

	public String buildStopRow() {
		logger.debug("this.meaEndTime = {}", this.meaEndTime);
		LocalDateTime dateTime = LocalDateTime.parse(meaEndTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		long eMill = dateTime.toEpochSecond(ZoneOffset.ofHours((8))) * 1000;
		String stopRow = cityId + "_" + eMill + "_~";
		return stopRow;
	}

	public String buildTable() {

		String table = "RNO_4G_MR_DESC";
		return table;
	}

	@Override
	public String toString() {
		return "G4MrDescQueryCond [cityId=" + cityId + ", factory=" + factory + ", meaBegTime=" + meaBegTime
				+ ", meaEndTime=" + meaEndTime + "]";
	}
}
