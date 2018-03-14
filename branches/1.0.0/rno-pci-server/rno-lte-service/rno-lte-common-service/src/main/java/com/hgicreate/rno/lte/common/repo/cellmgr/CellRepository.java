package com.hgicreate.rno.lte.common.repo.cellmgr;

import com.hgicreate.rno.lte.common.model.cellmgr.Cell;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

public interface CellRepository extends JpaRepository<Cell, Long> {
    Page<Cell> findAll(Specification<Cell> spec, Pageable pageable);

    List<Cell> findAll(Specification<Cell> spec);

    List<Cell> findByAreaId(long areaId);

    Cell findByCellId(String cellId);

    List<Cell> findByCellIdNotAndEnodebId(String cellId, String enodebId);
}