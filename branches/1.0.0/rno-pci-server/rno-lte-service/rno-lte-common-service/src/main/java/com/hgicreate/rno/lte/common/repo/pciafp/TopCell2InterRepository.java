package com.hgicreate.rno.lte.common.repo.pciafp;

import com.hgicreate.rno.lte.common.model.pciafp.TopCell2Inter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

public interface TopCell2InterRepository extends JpaRepository<TopCell2Inter, Long> {
    List<TopCell2Inter> findByJobIdAndPlanNum(@Param("jobId") long jobId, @Param("planNum") int planNum);
}