package com.hgicreate.rno.ltestrucanlsclient.model;

public class Page {

	private long totalPageCnt = 0;// 总页数
	private int pageSize = 25;// 每页记录数
	private int currentPage = 1;// 当前页数，从1开始
	private long totalCnt = -1;// 总记录数

	private int forcedStartIndex = -1;// 如果这个值大于0，说明直接使用这个值，而不是使用currentPage*pageSize这样计算得到

	public long getTotalPageCnt() {
		return totalPageCnt;
	}

	public void setTotalPageCnt(long totalPageCnt) {
		this.totalPageCnt = totalPageCnt;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public long getTotalCnt() {
		return totalCnt;
	}

	public void setTotalCnt(long totalCnt) {
		this.totalCnt = totalCnt;
	}

	public int getForcedStartIndex() {
		return forcedStartIndex;
	}

	public void setForcedStartIndex(int forcedStartIndex) {
		this.forcedStartIndex = forcedStartIndex;
	}

	public int calculateStart() {
		if (forcedStartIndex > 0) {
			return forcedStartIndex;
		} else {
			return (this.getCurrentPage() - 1) * this.getPageSize();
		}
	}

	@Override
	public String toString() {
		return "Page [totalPageCnt=" + totalPageCnt + ", pageSize=" + pageSize + ", currentPage=" + currentPage
				+ ", totalCnt=" + totalCnt + ", forcedStartIndex=" + forcedStartIndex + "]";
	}

}
