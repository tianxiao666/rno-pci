package com.hgicreate.rno.lte.common.repo.pciafp;

import com.hgicreate.rno.lte.common.model.pciafp.PlanItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author chen.c10
 */
public interface BestPlanItemRepository extends JpaRepository<PlanItem, Long> {
    /**
     * 根据job id 获取最佳方案列表
     *
     * @param jobId job id
     * @return 最佳方案列表
     */
    List<PlanItem> findByJobId(long jobId);

    /**
     * 根据job id 删除最佳方案
     *
     * @param jobId job id
     */
    void deleteByJobId(long jobId);
}