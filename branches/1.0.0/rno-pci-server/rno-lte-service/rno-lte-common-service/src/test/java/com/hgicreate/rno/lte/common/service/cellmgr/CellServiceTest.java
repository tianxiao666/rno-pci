package com.hgicreate.rno.lte.common.service.cellmgr;

import com.hgicreate.rno.lte.common.model.cellmgr.Cell;
import com.hgicreate.rno.lte.common.model.cellmgr.CellMgrCond;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CellServiceTest {
    @Autowired
    private CellService cellService;

    @Test
    public void findAll(){
        CellMgrCond cond= new CellMgrCond();
        List<Long> cityIds = new ArrayList<>();
        cityIds.add(440100L);
        cond.setAreaIds(cityIds);
        cond.setCellId("66");
        cond.setENodeB("96");
        cond.setCellName("白云");

        List<Cell> cells= cellService.findAll(cond);
        System.out.println(cells);
    }

    @Test
    public void findAll2(){
        CellMgrCond cond= new CellMgrCond();
        List<Long> cityIds = new ArrayList<>();
        cityIds.add(440100L);
        cond.setAreaIds(cityIds);
        cond.setCellId("66");
        cond.setENodeB("96");
        cond.setCellName("白云");
        cond.setCurrentPage(0);
        cond.setPageSize(5);

        Pageable pageable = new PageRequest(cond.getCurrentPage(),cond.getPageSize());
        Page<Cell> cells= cellService.findAll(cond,pageable);
        System.out.println(cells.getContent());
    }

    @Test
    public void getSameStationCellsByCellId(){
        List<Cell> cells= cellService.getSameStationCellsByCellId("188184-2");
        System.out.println(cells);
    }
}