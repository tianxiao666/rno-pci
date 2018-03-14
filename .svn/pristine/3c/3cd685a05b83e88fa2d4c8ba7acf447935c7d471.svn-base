package com.hgicreate.rno.lte.common.mapper.azimutheval;

import com.hgicreate.rno.lte.common.model.TaskQueryResultDTO;
import com.hgicreate.rno.lte.common.model.azimutheval.AzimuthEvalTask;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AzimuthEvalTaskQueryResultMapper {
    AzimuthEvalTaskQueryResultMapper INSTANCE = Mappers.getMapper(AzimuthEvalTaskQueryResultMapper.class);

    @Mapping(source = "area.name", target = "cityName")
    @Mapping(source = "jobId", target = "jobId")
    @Mapping(source = "job.jobName", target = "jobName")
    @Mapping(source = "job.jobRunningStatus", target = "jobRunningStatus")
    @Mapping(source = "begMeaTime", target = "begMeaTime")
    @Mapping(source = "endMeaTime", target = "endMeaTime")
    @Mapping(source = "job.launchTime", target = "launchTime")
    @Mapping(source = "job.completeTime", target = "completeTime")
    TaskQueryResultDTO azimuthEvalTaskToTaskQueryResultDto(AzimuthEvalTask task);
}
