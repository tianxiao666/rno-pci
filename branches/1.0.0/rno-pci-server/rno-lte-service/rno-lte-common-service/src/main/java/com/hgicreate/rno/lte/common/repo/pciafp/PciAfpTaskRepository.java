package com.hgicreate.rno.lte.common.repo.pciafp;

import com.hgicreate.rno.lte.common.model.pciafp.PciAfpTask;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author chen.c10
 */
public interface PciAfpTaskRepository extends JpaRepository<PciAfpTask, Long> {
    Page<PciAfpTask> findAll(Specification<PciAfpTask> spec, Pageable pageable);

    PciAfpTask getByJobId(Long jobId);
}
