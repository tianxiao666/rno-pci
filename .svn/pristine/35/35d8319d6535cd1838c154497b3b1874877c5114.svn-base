package com.hgicreate.rno.lte.common.service.cellmgr;

import com.hgicreate.rno.lte.common.model.cellmgr.Cell;
import com.hgicreate.rno.lte.common.model.cellmgr.CellMgrCond;
import com.hgicreate.rno.lte.common.repo.cellmgr.CellRepository;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.persistence.criteria.Predicate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CellService {
    private final CellRepository cellRepository;

    public CellService(CellRepository cellRepository) {
        this.cellRepository = cellRepository;
    }

    public Page<Cell> findAll(CellMgrCond cond, Pageable pageable) {
        return cellRepository.findAll(Specifications.where(getWhereClause(cond)), pageable);
    }

    public List<Cell> findAll(CellMgrCond cond) {
        return cellRepository.findAll(Specifications.where(getWhereClause(cond)));
    }

    private Specification<Cell> getWhereClause(CellMgrCond cond) {
        return (root, query, builder) -> {
            Predicate predicate = builder.conjunction();
            if (!CollectionUtils.isEmpty(cond.getAreaIds())) {
                predicate.getExpressions().add(
                        root.get("areaId").in(cond.getAreaIds())
                );
            }
            if (StringUtils.isNotBlank(cond.getCellId())) {
                predicate.getExpressions().add(
                        builder.like(root.get("cellId"), "%" + cond.getCellId() + "%")
                );
            }
            if (StringUtils.isNotBlank(cond.getCellName())) {
                predicate.getExpressions().add(
                        builder.like(root.get("cellName"), "%" + cond.getCellName() + "%")
                );
            }
            if (StringUtils.isNotBlank(cond.getENodeB())) {
                predicate.getExpressions().add(
                        builder.like(root.get("enodebId"), "%" + cond.getENodeB() + "%")
                );
            }
            if (null != cond.getPci() && cond.getPci() >= 0 && cond.getPci() < 504) {
                predicate.getExpressions().add(
                        builder.equal(root.get("pci"), cond.getPci())
                );
            }
            return predicate;
        };
    }

    public List<Cell> findByAreaId(long areaId) {
        return cellRepository.findByAreaId(areaId);
    }

    public List<Cell> getSameStationCellsByCellId(String cellId) {
        Cell cell = cellRepository.findByCellId(cellId);
        List<Cell> cells = cellRepository.findByCellIdNotAndEnodebId(cellId, cell.getEnodebId());
        cells.add(0, cell);
        return cells;
    }

    public Boolean changeCellPci(String cellId, int pci) {
        Cell cell = cellRepository.findByCellId(cellId);
        cell.setPci(pci);
        return null != cellRepository.save(cell);
    }

    public Map<String, Cell> getCellMapByAreaId(long areaId) {
        List<Cell> cells = cellRepository.findByAreaId(areaId);
        if (cells == null || cells.size() == 0) {
            return Collections.emptyMap();
        }
        Map<String, Cell> cellMap = new HashMap<>(cells.size());
        for (Cell cell : cells) {
            cellMap.put(cell.getCellId(), cell);
        }
        return cellMap;
    }
}