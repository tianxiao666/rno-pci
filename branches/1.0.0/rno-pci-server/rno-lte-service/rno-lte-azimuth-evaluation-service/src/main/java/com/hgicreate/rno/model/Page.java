package com.hgicreate.rno.model;

import lombok.Data;

/**
 * 分页结果对象
 *
 * @author li.tf
 * @date 2018-01-12 14:11:33
 */
@Data
public class Page {
    private long totalPageCnt = 0;
    private long totalCnt = 0;
    private int pageSize = 25;
    private int currentPage = 1;
    private int forcedStartIndex = -1;

}
