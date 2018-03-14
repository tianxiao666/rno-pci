package com.hgicreate.rno.lte.common.repo.pciafp;

import com.hgicreate.rno.lte.common.model.pciafp.PciAfpParam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PciAfpParamRepository extends JpaRepository<PciAfpParam, Long> {
    List<PciAfpParam> findByJobId(@Param("jobId") long jobId);
}