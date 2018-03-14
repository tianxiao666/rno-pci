package com.hgicreate.rno.lte.common.repo.pciafp;

import com.hgicreate.rno.lte.common.model.pciafp.D1Cell2Inter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

public interface D1Cell2InterRepository extends JpaRepository<D1Cell2Inter, Long> {
    List<D1Cell2Inter> findByJobId(@Param("jobId") long jobId);
}