package com.hgicreate.rno.lte.common.service.azimutheval;

import com.hgicreate.rno.lte.common.mapper.AzimuthEvaluationMapper;
import com.hgicreate.rno.lte.common.model.azimutheval.AzimuthEvalResult;
import com.hgicreate.rno.lte.common.repo.azimutheval.AzimuthEvalResultRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class AzimuthEvalResultService {
    private final AzimuthEvalResultRepository azimuthEvalResultRepository;
    private final AzimuthEvaluationMapper azimuthEvaluationMapper;

    public AzimuthEvalResultService(AzimuthEvalResultRepository azimuthEvalResultRepository,
                                    AzimuthEvaluationMapper azimuthEvaluationMapper) {
        this.azimuthEvalResultRepository = azimuthEvalResultRepository;
        this.azimuthEvaluationMapper = azimuthEvaluationMapper;
    }

    public List<Map<String, Object>> findAzimuthEvalResultsByJobId(long jobId) {
        return azimuthEvaluationMapper.findAzimuthEvalResultsByJobId(jobId);
    }

    public List<Map<String, Object>> findTop1000AzimuthEvalResultsByJobId(long jobId) {
        return azimuthEvaluationMapper.findTop1000AzimuthEvalResultsByJobId(jobId);
    }

    public List<AzimuthEvalResult> saveAzimuthEvalResults(List<AzimuthEvalResult> azimuthEvalResults) {
        return azimuthEvalResultRepository.save(azimuthEvalResults);
    }

}
