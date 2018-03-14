package com.hgicreate.rno.lte.pciafp.model;

import lombok.Data;

@Data
public class Page {
    private int pageSize = 25;// 每页记录数
    private int currentPage = 1;// 当前页数，从1开始
    private long totalCnt = 0;// 总记录数
    private long totalPageCnt = -1;// 总页数
    private int forcedStartIndex = -1;// 如果这个值大于0，说明直接使用这个值，而不是使用currentPage*pageSize这样计算得到

    public int calculateStart() {
        if (forcedStartIndex > 0) {
            return forcedStartIndex;
        } else {
            return (this.getCurrentPage() - 1) * this.getPageSize();
        }
    }
}
