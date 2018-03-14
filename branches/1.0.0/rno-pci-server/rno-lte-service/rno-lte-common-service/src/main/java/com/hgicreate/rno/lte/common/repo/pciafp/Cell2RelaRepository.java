package com.hgicreate.rno.lte.common.repo.pciafp;

import com.hgicreate.rno.lte.common.model.pciafp.Cell2Rela;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

public interface Cell2RelaRepository extends JpaRepository<Cell2Rela, Long> {
    List<Cell2Rela> findByJobId(@Param("jobId") long jobId);
}