package com.hgicreate.rno.ltestrucanlsclient.model;

/**
 * Created by chen.c10 on 2016/12/26.
 * 过覆盖
 */
public class OverCover {
    private String cellId;
    private String cellName;
    private int cellPci;
    private int cellEarfcn;
    private double cellLon;
    private double cellLat;
    private double stationSpace;
    private String ncellId;
    private String ncellName;
    private int ncellPci;
    private int ncellEarfcn;
    private double ncellLon;
    private double ncellLat;
    private int ncellCnt;
    private int totalCnt;
    private double ncellPer;
    private double dis;

    @Override
    public String toString() {
        return "OverCover{" +
                "cellId='" + cellId + '\'' +
                ", cellName='" + cellName + '\'' +
                ", cellPci=" + cellPci +
                ", cellEarfcn=" + cellEarfcn +
                ", cellLon=" + cellLon +
                ", cellLat=" + cellLat +
                ", stationSpace=" + stationSpace +
                ", ncellId='" + ncellId + '\'' +
                ", ncellName='" + ncellName + '\'' +
                ", ncellPci=" + ncellPci +
                ", ncellEarfcn=" + ncellEarfcn +
                ", ncellLon=" + ncellLon +
                ", ncellLat=" + ncellLat +
                ", ncellCnt=" + ncellCnt +
                ", totalCnt=" + totalCnt +
                ", ncellPer=" + ncellPer +
                ", dis=" + dis +
                '}';
    }

    public String getCellId() {
        return cellId;
    }

    public void setCellId(String cellId) {
        this.cellId = cellId;
    }

    public String getCellName() {
        return cellName;
    }

    public void setCellName(String cellName) {
        this.cellName = cellName;
    }

    public int getCellPci() {
        return cellPci;
    }

    public void setCellPci(int cellPci) {
        this.cellPci = cellPci;
    }

    public int getCellEarfcn() {
        return cellEarfcn;
    }

    public void setCellEarfcn(int cellEarfcn) {
        this.cellEarfcn = cellEarfcn;
    }

    public double getCellLon() {
        return cellLon;
    }

    public void setCellLon(double cellLon) {
        this.cellLon = cellLon;
    }

    public double getCellLat() {
        return cellLat;
    }

    public void setCellLat(double cellLat) {
        this.cellLat = cellLat;
    }

    public double getStationSpace() {
        return stationSpace;
    }

    public void setStationSpace(double stationSpace) {
        this.stationSpace = stationSpace;
    }

    public String getNcellId() {
        return ncellId;
    }

    public void setNcellId(String ncellId) {
        this.ncellId = ncellId;
    }

    public String getNcellName() {
        return ncellName;
    }

    public void setNcellName(String ncellName) {
        this.ncellName = ncellName;
    }

    public int getNcellPci() {
        return ncellPci;
    }

    public void setNcellPci(int ncellPci) {
        this.ncellPci = ncellPci;
    }

    public int getNcellEarfcn() {
        return ncellEarfcn;
    }

    public void setNcellEarfcn(int ncellEarfcn) {
        this.ncellEarfcn = ncellEarfcn;
    }

    public double getNcellLon() {
        return ncellLon;
    }

    public void setNcellLon(double ncellLon) {
        this.ncellLon = ncellLon;
    }

    public double getNcellLat() {
        return ncellLat;
    }

    public void setNcellLat(double ncellLat) {
        this.ncellLat = ncellLat;
    }

    public int getNcellCnt() {
        return ncellCnt;
    }

    public void setNcellCnt(int ncellCnt) {
        this.ncellCnt = ncellCnt;
    }

    public int getTotalCnt() {
        return totalCnt;
    }

    public void setTotalCnt(int totalCnt) {
        this.totalCnt = totalCnt;
    }

    public double getNcellPer() {
        return ncellPer;
    }

    public void setNcellPer(double ncellPer) {
        this.ncellPer = ncellPer;
    }

    public double getDis() {
        return dis;
    }

    public void setDis(double dis) {
        this.dis = dis;
    }
}
