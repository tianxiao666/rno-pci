package com.hgicreate.rno.lte.common.repo.pciafp;

import com.hgicreate.rno.lte.common.model.pciafp.NcCheckPlanItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

public interface NcCheckPlanItemRepository extends JpaRepository<NcCheckPlanItem, Long> {
    List<NcCheckPlanItem> findByJobId(@Param("jobId") long jobId);
}