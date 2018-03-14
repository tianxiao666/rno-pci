package com.hgicreate.rno.lte.pciafp.service;

import com.hgicreate.rno.lte.pciafp.mapper.DataMapper;
import com.hgicreate.rno.lte.pciafp.model.Cell;
import com.hgicreate.rno.lte.pciafp.model.InterMatrix;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author chen.c10
 */
@Slf4j
@Service
public class PciAfpDataServiceImpl implements PciAfpDataService {

    private final DataMapper dataMapper;

    public PciAfpDataServiceImpl(DataMapper dataMapper) {
        this.dataMapper = dataMapper;
    }

    @Override
    public Map<String, Cell> getLteCellMapByCityId(long cityId) {
        log.debug("进入方法：getLteCellMapByCityId。cityId=" + cityId);
        List<Cell> lteCells = dataMapper.getLteCellsByCityId(cityId);
        if (lteCells == null || lteCells.size() == 0) {
            return Collections.emptyMap();
        }
        Map<String, Cell> cellMap = new HashMap<>(5000);
        for (Cell cell : lteCells) {
            cellMap.put(cell.getId(), cell);
        }
        return cellMap;
    }

    @Override
    public List<InterMatrix> getLatestMatrixByCityId(long cityId) {
        return dataMapper.getLatestMatrixByCityId(cityId);
    }

    @Override
    public List<InterMatrix> getLatestMatrixByCityIdAndCells(long cityId, List<String> cells){
        return dataMapper.getLatestMatrixByCityIdAndCells(cityId,cells);
    }
}
