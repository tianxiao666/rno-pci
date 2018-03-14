package com.hgicreate.rno.lte.common.repo.cellmgr;

import com.hgicreate.rno.lte.common.model.cellmgr.Cell;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CellRepositoryTest {
    @Autowired
    private CellRepository cellRepository;

    @Test
    public void findAll() throws Exception {
        List<Cell> cellList = cellRepository.findAll();
        System.out.println(cellList.size());
        System.out.println(cellList.get(0));
    }

    @Test
    public void findAll1() throws Exception {
    }

    @Test
    public void findAll2() throws Exception {
    }

    @Test
    public void findByCellIdNotAndEnodebId() throws Exception {
        List<Cell> cellList = cellRepository.findByCellIdNotAndEnodebId("188184-2","188184");
        System.out.println(cellList);
    }
}