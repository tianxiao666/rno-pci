package com.hgicreate.rno.lte.common.service.mapper;

import com.hgicreate.rno.lte.common.model.Report;
import com.hgicreate.rno.lte.common.service.dto.ReportDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author chen.c10
 */
@Mapper
public interface ReportMapper extends EntityMapper<ReportDTO, Report> {
    ReportMapper INSTANCE = Mappers.getMapper(ReportMapper.class);

    /**
     * 对象转换为dto
     * @param report 报告对象
     * @return 报告dto
     */
    @Override
    @Mapping(source = "stage", target = "stage")
    @Mapping(source = "begTime", target = "startTime")
    @Mapping(source = "endTime", target = "endTime")
    @Mapping(source = "state", target = "result")
    @Mapping(source = "attMsg", target = "detail")
    ReportDTO toDto(Report report);
}
