package com.hgicreate.rno.lte.common.repo.pciafp;

import com.hgicreate.rno.lte.common.model.pciafp.DataCollectRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface DataCollectRecordRepository extends JpaRepository<DataCollectRecord, Long> {
    DataCollectRecord findOneByJobId(@Param("jobId") long jobId);
}