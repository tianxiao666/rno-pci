package com.hgicreate.rno.lte.common.repo.azimutheval;

import com.hgicreate.rno.lte.common.model.azimutheval.AzimuthEvalResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

public interface AzimuthEvalResultRepository extends JpaRepository<AzimuthEvalResult, Long> {
    List<AzimuthEvalResult> findByJobId(@Param("jobId") long jobId);

    List<AzimuthEvalResult> findTop1000ByJobId(@Param("jobId") long jobId);
}