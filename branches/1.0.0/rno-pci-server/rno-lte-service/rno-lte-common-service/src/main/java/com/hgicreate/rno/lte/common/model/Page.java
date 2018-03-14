package com.hgicreate.rno.lte.common.model;

import lombok.Data;

/**
 * @author chen.c10
 */
@Data
public class Page implements Cloneable {
    private long totalPageCnt = 0;
    private long totalCnt = 0;
    private int pageSize = 25;
    private int currentPage = 1;
    /**
     如果这个值大于0，说明直接使用这个值，而不是使用currentPage*pageSize这样计算得到
     */
    private int forcedStartIndex = -1;

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public int calculateStart() {
        if (forcedStartIndex > 0) {
            return forcedStartIndex;
        } else {
            return (this.getCurrentPage() - 1) * this.getPageSize();
        }
    }
}
