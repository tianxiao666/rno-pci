package com.hgicreate.rno.lte.common.mapper.azimutheval;

import com.hgicreate.rno.lte.common.model.TaskQueryResultDTO;
import com.hgicreate.rno.lte.common.model.azimutheval.AzimuthEvalResult;
import com.hgicreate.rno.lte.common.model.azimutheval.AzimuthEvalTask;
import com.hgicreate.rno.lte.common.model.azimutheval.AzimuthEvaluationResultDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AzimuthEvaluationResultMapper {
    AzimuthEvaluationResultMapper INSTANCE = Mappers.getMapper(AzimuthEvaluationResultMapper.class);

    @Mapping(source = "area.name", target = "cityName")
    @Mapping(source = "lteCell.cellName", target = "cellName")
    @Mapping(source = "lteCell.cellId", target = "cellId")
    @Mapping(source = "azimuth", target = "oldAzimuth")
    @Mapping(source = "azimuth1", target = "newAzimuth")
    @Mapping(source = "diff", target = "disValue")
    AzimuthEvaluationResultDTO azimuthEvalResultToAzimuthEvaluationResultDto(AzimuthEvalResult result);
}
