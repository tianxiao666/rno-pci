package com.hgicreate.rno.lte.common.model.cellmgr;

import lombok.Data;

import java.util.Collection;

/**
 * @author chen.c10
 */
@Data
public class CellMgrCond {
    private Collection<Long> areaIds;
    private String eNodeB;
    private String cellId;
    private String cellName;
    private Integer pci;
    /**
     * 当前页码，下标从0开始
     */
    private int currentPage;
    private int pageSize;
}
