package com.hgicreate.rno.lte.common.repo.azimutheval;

import com.hgicreate.rno.lte.common.model.azimutheval.AzimuthEvalTask;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AzimuthEvalTaskRepository extends JpaRepository<AzimuthEvalTask, Long> {
    List<AzimuthEvalTask> findAll(Specification<AzimuthEvalTask> spec);
    AzimuthEvalTask findOneByJobId(long jobId);
}
