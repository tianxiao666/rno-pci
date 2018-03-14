package com.hgicreate.rno.lte.common.repo.pciafp;

import com.hgicreate.rno.lte.common.model.pciafp.Threshold;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ThresholdRepository extends JpaRepository<Threshold, Long> {
    List<Threshold> findByModuleTypeOrderByOrderNum(String moduleType);
}
