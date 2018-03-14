package com.hgicreate.rno.model;

import java.io.Serializable;

public class Cell implements Serializable {

	private static final long serialVersionUID = 1L;

	String cellId;
	double lon;
	double lat;
	String bcch;
	int pci;
	String enodebId;

	public Cell() {
	}

	public Cell(String cellId, double lon, double lat, String bcch, int pci, String enodebId) {
		this.cellId = cellId;
		this.lon = lon;
		this.lat = lat;
		this.bcch = bcch;
		this.pci = pci;
		this.enodebId = enodebId;
	}

	public String getCellId() {
		return cellId;
	}

	public void setCellId(String cellId) {
		this.cellId = cellId;
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

	public String getBcch() {
		return bcch;
	}

	public void setBcch(String bcch) {
		this.bcch = bcch;
	}

	public int getPci() {
		return pci;
	}

	public void setPci(int pci) {
		this.pci = pci;
	}

	public String getEnodebId() {
		return enodebId;
	}

	public void setEnodebId(String enodebId) {
		this.enodebId = enodebId;
	}

	@Override
	public String toString() {
		return "Cell [cellId=" + cellId + ", lon=" + lon + ", lat=" + lat + ", bcch=" + bcch + ", pci=" + pci
				+ ", enodebId=" + enodebId + "]";
	}
}
