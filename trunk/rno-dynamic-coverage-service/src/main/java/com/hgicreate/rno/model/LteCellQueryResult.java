package com.hgicreate.rno.model;

import java.util.List;

public class LteCellQueryResult {
	private int totalCnt;
	private List<RnoLteCell> lteCells;
	private Page page;
	
	public int getTotalCnt() {
		return totalCnt;
	}
	public void setTotalCnt(int totalCnt) {
		this.totalCnt = totalCnt;
	}


	public List<RnoLteCell> getLteCells() {
		return lteCells;
	}
	public void setLteCells(List<RnoLteCell> lteCells) {
		this.lteCells = lteCells;
	}
	public Page getPage() {
		return page;
	}

	public void setPage(Page page) {
		this.page = page;
	}
}
