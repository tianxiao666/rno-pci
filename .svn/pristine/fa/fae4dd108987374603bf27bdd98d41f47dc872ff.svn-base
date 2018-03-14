package com.hgicreate.rno.lte.common.mapper.azimutheval;

import com.hgicreate.rno.lte.common.model.azimutheval.AzimuthEvalResult;
import com.hgicreate.rno.lte.common.model.azimutheval.AzimuthEvaluationResultDTO;
import com.hgicreate.rno.lte.common.model.azimutheval.NetCoverageEnhancePlanDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AzimuthEvaluationEnhanceMapper {
    AzimuthEvaluationEnhanceMapper INSTANCE = Mappers.getMapper(AzimuthEvaluationEnhanceMapper.class);

    @Mapping(source = "area.name", target = "cityName")
    @Mapping(source = "lteCell.cellName", target = "cellName")
    @Mapping(source = "cellId", target = "cellId")
    @Mapping(target = "result", ignore = true)
    NetCoverageEnhancePlanDTO azimuthEvalResultToNetCoverageEnhancePlanDto(AzimuthEvalResult result);
}
