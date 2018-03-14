package com.hgicreate.rno.lte.common.controller.cellmgr;

import com.hgicreate.rno.lte.common.model.cellmgr.Cell;
import com.hgicreate.rno.lte.common.model.cellmgr.CellMgrCond;
import com.hgicreate.rno.lte.common.service.cellmgr.CellService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author chen.c10
 */
@Slf4j
@RestController
@RequestMapping("/cellMgr")
public class CellMgrController {

    private final CellService cellService;

    public CellMgrController(CellService cellService) {
        this.cellService = cellService;
    }

    @PostMapping("/findByPage")
    public PagedResources<Resource<Cell>> findByPage(@RequestBody CellMgrCond cond, PagedResourcesAssembler<Cell> assembler) {
        log.debug("进入方法：findByPage。cond={}", cond);
        Pageable pageable = new PageRequest(cond.getCurrentPage(), cond.getPageSize());
        Page<Cell> cells = cellService.findAll(cond, pageable);
        log.debug("退出方法：findByPage。cells.size={}", cells.getSize());
        log.debug("退出方法：findByPage。cells={}", cells.getContent());
        PagedResources<Resource<Cell>> pagedResources = assembler.toResource(cells);
        log.debug("退出方法：findByPage。pagedResources.size={}", pagedResources.getMetadata().getSize());
        log.debug("退出方法：findByPage。pagedResources={}", pagedResources.getContent());
        return pagedResources;
    }

    @PostMapping("/findByPage2")
    public PagedResources<Cell> findByPage2(@RequestBody CellMgrCond cond) {
        log.debug("进入方法：findByPage。cond={}", cond);
        Pageable pageable = new PageRequest(cond.getCurrentPage(), cond.getPageSize());
        Page<Cell> cells = cellService.findAll(cond, pageable);
        log.debug("退出方法：findByPage。cells.size={}", cells.getSize());
        log.debug("退出方法：findByPage。cells={}", cells.getContent());
        PagedResources.PageMetadata pageMetadata = new PagedResources.PageMetadata(cells.getSize(), cells.getNumber(), cells.getTotalElements(), cells.getTotalPages());
        return new PagedResources<>(cells.getContent(), pageMetadata);
    }

    @PostMapping("/findAll")
    public List<Cell> findAll(@RequestBody CellMgrCond cond) {
        log.debug("进入方法：findAll。cond={}", cond);
        return cellService.findAll(cond);
    }

    @GetMapping("/findByAreaId")
    public List<Cell> findByAreaId(long areaId) {
        log.debug("进入方法：findByAreaId。areaId={}", areaId);
        return cellService.findByAreaId(areaId);
    }

    @PostMapping("/getSameStationCellsByCellId")
    public List<Cell> getSameStationCellsByCellId(String cellId) {
        log.debug("进入方法：getSameStationCellsByCellId。cellId={}", cellId);
        List<Cell> cells = cellService.getSameStationCellsByCellId(cellId);
        log.debug("退出方法：getSameStationCellsByCellId。cells={}", cells);
        return cells;
    }

    @PutMapping("/changeCellPci/{cellId}")
    public Boolean changeCellPci(@PathVariable("cellId") String cellId, int pci) {
        log.debug("进入方法：getSameStationCellsByCellId。cellId={}", cellId);
        return cellService.changeCellPci(cellId, pci);
    }

    @GetMapping("/test")
    public PagedResources<Resource<Cell>> test(PagedResourcesAssembler<Cell> assembler) {
        CellMgrCond cond = new CellMgrCond();
        log.debug("进入方法：test。cond={}", cond);
        Pageable pageable = new PageRequest(cond.getCurrentPage(), 5);
        Page<Cell> cells = cellService.findAll(cond, pageable);
        log.debug("退出方法：test。cells.size={}", cells.getSize());
        PagedResources<Resource<Cell>> pagedResources = assembler.toResource(cells);
        log.debug("退出方法：test。pagedResources.size={}", pagedResources.getMetadata().getSize());
        return pagedResources;
    }
}
