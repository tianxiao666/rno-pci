package com.hgicreate.rno.lte.common.model.azimutheval;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AzimuthEvaluationResultDTO {

    private String cityName;
    private String cellName;
    private String cellId;
    private Integer oldAzimuth;
    private Integer newAzimuth;
    private Integer disValue;

    public AzimuthEvaluationResultDTO(AzimuthEvalResult result) {
        this.cityName = result.getArea().getName();
        this.cellName = result.getLteCell().getCellName();
        this.cellId = result.getCellId();
        this.oldAzimuth = result.getAzimuth();
        this.newAzimuth = result.getAzimuth1();
        this.disValue = result.getDiff();
    }

}
