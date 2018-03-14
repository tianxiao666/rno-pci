package com.hgicreate.rno.ltestrucanlsclient.model;

import java.io.Serializable;
import java.util.Date;

public class Area implements Serializable {
	private static final long serialVersionUID = 1L;

	private long areaId;
	private String name;
	private long parentId;
	private String areaLevel;
	private String entityType;
	private long entityId;
	private double lon;
	private double lat;
	private String path;
	private Date createTime;
	private Date updateTime;

	public long getParentId() {
		return parentId;
	}

	public void setParentId(long parentId) {
		this.parentId = parentId;
	}

	public long getAreaId() {
		return areaId;
	}

	public void setAreaId(long areaId) {
		this.areaId = areaId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAreaLevel() {
		return areaLevel;
	}

	public void setAreaLevel(String areaLevel) {
		this.areaLevel = areaLevel;
	}

	public String getEntityType() {
		return entityType;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}

	public long getEntityId() {
		return entityId;
	}

	public void setEntityId(long entityId) {
		this.entityId = entityId;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	@Override
	public String toString() {
		return "Area [areaId=" + areaId + ", name=" + name + ", parentId=" + parentId + ", areaLevel=" + areaLevel
				+ ", entityType=" + entityType + ", entityId=" + entityId + ", lon=" + lon + ", lat=" + lat + ", path="
				+ path + ", createTime=" + createTime + ", updateTime=" + updateTime + "]";
	}
}
