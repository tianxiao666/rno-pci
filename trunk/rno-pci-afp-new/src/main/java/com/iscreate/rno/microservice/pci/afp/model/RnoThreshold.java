package com.iscreate.rno.microservice.pci.afp.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class RnoThreshold implements Serializable {

	private static final long serialVersionUID = 1L;
	private BigDecimal id;
	private long orderNum;
	private String moduleType;
	private String code;
	private String descInfo;
	private String defaultVal;
	private String scopeDesc;
	private String conditionGroup;
	private boolean flag = true;

	public RnoThreshold() {
	}

	/** full constructor */
	public RnoThreshold(long orderNum, String moduleType, String code, String descInfo, String defaultVal,
			String scopeDesc, boolean flag) {
		this.orderNum = orderNum;
		this.moduleType = moduleType;
		this.code = code;
		this.descInfo = descInfo;
		this.defaultVal = defaultVal;
		this.scopeDesc = scopeDesc;
		this.flag = flag;
	}

	// Property accessors

	public BigDecimal getId() {
		return this.id;
	}

	public void setId(BigDecimal id) {
		this.id = id;
	}

	public long getOrderNum() {
		return this.orderNum;
	}

	public void setOrderNum(long orderNum) {
		this.orderNum = orderNum;
	}

	public String getModuleType() {
		return this.moduleType;
	}

	public void setModuleType(String moduleType) {
		this.moduleType = moduleType;
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescInfo() {
		return this.descInfo;
	}

	public void setDescInfo(String descInfo) {
		this.descInfo = descInfo;
	}

	public String getDefaultVal() {
		return this.defaultVal;
	}

	public void setDefaultVal(String defaultVal) {
		this.defaultVal = defaultVal;
	}

	public String getScopeDesc() {
		return this.scopeDesc;
	}

	public void setScopeDesc(String scopeDesc) {
		this.scopeDesc = scopeDesc;
	}

	public String getConditionGroup() {
		return conditionGroup;
	}

	public void setConditionGroup(String conditionGroup) {
		this.conditionGroup = conditionGroup;
	}

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	@Override
	public String toString() {
		return "RnoThreshold [id=" + id + ", orderNum=" + orderNum + ", moduleType=" + moduleType + ", code=" + code
				+ ", descInfo=" + descInfo + ", defaultVal=" + defaultVal + ", scopeDesc=" + scopeDesc
				+ ", conditionGroup=" + conditionGroup + ", flag=" + flag + "]";
	}

}