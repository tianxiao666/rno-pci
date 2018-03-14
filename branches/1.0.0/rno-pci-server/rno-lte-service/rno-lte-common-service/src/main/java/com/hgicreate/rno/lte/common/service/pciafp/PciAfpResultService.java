package com.hgicreate.rno.lte.common.service.pciafp;

import com.hgicreate.rno.lte.common.model.Area;
import com.hgicreate.rno.lte.common.model.cellmgr.Cell;
import com.hgicreate.rno.lte.common.model.pciafp.PlanItem;
import com.hgicreate.rno.lte.common.model.pciafp.PciAfpTask;
import com.hgicreate.rno.lte.common.repo.AreaRepository;
import com.hgicreate.rno.lte.common.repo.pciafp.BestPlanItemRepository;
import com.hgicreate.rno.lte.common.repo.pciafp.PciAfpTaskRepository;
import com.hgicreate.rno.lte.common.service.cellmgr.CellService;
import com.hgicreate.rno.lte.common.service.pciafp.dto.PciAfpResultDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author chen.c10
 */
@Slf4j
@Service
public class PciAfpResultService {
    private final AreaRepository areaRepository;
    private final CellService cellService;
    private final PciAfpTaskRepository pciAfpTaskRepository;
    private final BestPlanItemRepository bestPlanItemRepository;

    public PciAfpResultService(AreaRepository areaRepository, CellService cellService, PciAfpTaskRepository pciAfpTaskRepository, BestPlanItemRepository bestPlanItemRepository) {
        this.areaRepository = areaRepository;
        this.cellService = cellService;
        this.pciAfpTaskRepository = pciAfpTaskRepository;
        this.bestPlanItemRepository = bestPlanItemRepository;
    }

    @Transactional(rollbackOn = SQLException.class)
    public List<PlanItem> saveBestPlan(List<PlanItem> items) {
        if (null != items && items.size() > 0) {
            bestPlanItemRepository.deleteByJobId(items.get(0).getJobId());
        }
        return bestPlanItemRepository.save(items);
    }

    public List<PlanItem> queryBestPlanByJobId(long jobId) {
        return bestPlanItemRepository.findByJobId(jobId);
    }

    public List<PciAfpResultDTO> getPciAfpResultsByJobId(long jobId) {
        PciAfpTask taskInfo = pciAfpTaskRepository.getByJobId(jobId);
        log.debug("pci优化任务信息：{}",taskInfo);
        if (null == taskInfo) {
            log.info("不存在任务<{}>的信息", jobId);
            return null;
        }

        // 获取pci规划任务中的待优化小区字段
        String optimizeCellStr = taskInfo.getOptimizeCells();
        if (null == optimizeCellStr || optimizeCellStr.isEmpty()) {
            log.info("不存在任务<{}>的变PCI小区表！", jobId);
            return null;
        }

        List<String> optimizeCells = new ArrayList<>();
        for (String c : optimizeCellStr.trim().split(",")) {
            if (!"".equals(c.trim())) {
                optimizeCells.add(c);
            }
        }

        // 获取工参
        long cityId = taskInfo.getAreaId();
        Map<String, Cell> cellMap = cellService.getCellMapByAreaId(cityId);

        if (cellMap.isEmpty()) {
            Area area = areaRepository.getOne(cityId);
            if (null != area) {
                log.info("该区域<{}>不存在系统小区信息", area.getName());
            } else {
                log.info("该区域<{}>不存在系统小区信息", cityId);
            }
            return null;
        }

        // 获取方案
        List<PlanItem> planItems = bestPlanItemRepository.findByJobId(jobId);
        if (null == planItems) {
            log.info("不存在任务<{}>的方案", jobId);
            return null;
        }

        return collectResult(planItems, cellMap, optimizeCells);
    }

    private List<PciAfpResultDTO> collectResult(List<PlanItem> list, Map<String, Cell> cellMap, List<String> optimizeCells) {
        List<PciAfpResultDTO> result = new ArrayList<>();
        PciAfpResultDTO one;
        String cellId;
        Cell cell;
        for (PlanItem item : list) {
            one = new PciAfpResultDTO();
            cellId = item.getCellId();
            cell = cellMap.get(cellId);
            if (cell == null) {
                log.info("小区找不到对应工参数据：cellId ={}", cellId);
                one.setCellName("未知小区");
                one.setOldPci(-1);
                one.setOldEarfcn(-1);
            } else {
                // 小区名，pci,频点
                one.setCellName(cell.getCellName());
                one.setOldPci(cell.getPci());
                one.setOldEarfcn(cell.getEarfcn());
            }
            one.setCellId(cellId);
            int newEarfcn = item.getEarfcn();
            if (newEarfcn == -1) {
                if (cell == null) {
                    one.setNewEarfcn("找不到对应工参数据");
                } else {
                    one.setNewEarfcn("找不到对应MR数据");
                }
            } else {
                one.setNewEarfcn(Integer.toString(newEarfcn));
            }
            int newPci = item.getPci();
            if (newPci == -1) {
                if (cell == null) {
                    one.setNewPci("找不到对应工参数据");
                } else {
                    one.setNewPci("找不到对应MR数据");
                }
            } else {
                one.setNewPci(Integer.toString(newPci));
            }
            one.setOldInterVal(item.getOldInterVal());
            one.setNewInterVal(item.getNewInterVal());

            if (optimizeCells.contains(cellId)) {
                one.setRemark("修改小区");
            } else {
                one.setRemark("MR其他小区");
            }
            result.add(one);
        }
        return result;
    }
}
