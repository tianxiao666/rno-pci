package com.hgicreate.rno.lte.common.repo.pciafp;

import com.hgicreate.rno.lte.common.model.pciafp.MidPlanItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

public interface MidPlanItemRepository extends JpaRepository<MidPlanItem, Long> {
    Long countDistinctPlanNumByJobId(@Param("jobId") long jobId);

    List<MidPlanItem> findByJobIdAndPlanNum(@Param("jobId") long jobId, @Param("planNum") int planNum);
}