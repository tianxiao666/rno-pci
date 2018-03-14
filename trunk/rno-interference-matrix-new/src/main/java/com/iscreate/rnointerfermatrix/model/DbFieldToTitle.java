package com.iscreate.rnointerfermatrix.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class DbFieldToTitle {

	String dbField;
	int matchType;// 0：模糊匹配，1：精确匹配
	int index = -1;// 在文件中出现的位置，从0开始
	boolean isMandatory = true;// 是否强制要求出现
	private String dbType;// 类型：Number, String, Date
	List<String> titles = new ArrayList<String>();

	int sqlIndex = -1;// 在sql语句中的位置

	public String getDbField() {
		return dbField;
	}

	public void setDbField(String dbField) {
		this.dbField = dbField;
	}

	public int getMatchType() {
		return matchType;
	}

	public void setMatchType(int matchType) {
		this.matchType = matchType;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public List<String> getTitles() {
		return titles;
	}

	public void setTitles(List<String> titles) {
		this.titles = titles;
	}

	public boolean isMandatory() {
		return isMandatory;
	}

	public void setMandatory(boolean isMandatory) {
		this.isMandatory = isMandatory;
	}

	public String getDbType() {
		return dbType;
	}

	public void setDbType(String dbType) {
		this.dbType = dbType;
	}

	public void addTitle(String t) {
		if (!StringUtils.isBlank(t)) {
			titles.add(t);
		}
	}

	@Override
	public String toString() {
		return "DBFieldToTitle [dbField=" + dbField + ", matchType=" + matchType + ", index=" + index + ", isMandatory="
				+ isMandatory + ", dbType=" + dbType + ", titles=" + titles + "]";
	}
}
