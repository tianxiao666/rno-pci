package com.hgicreate.rno.lte.common.model.azimutheval;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NetCoverageEnhancePlanDTO {

    private String cityName;
    private String cellId;
    private String cellName;
    private Integer result;

    public NetCoverageEnhancePlanDTO(AzimuthEvalResult result) {
        this.cityName = result.getArea().getName();
        this.cellId = result.getCellId();
        this.cellName = result.getLteCell().getCellName();
        this.result = result.getAzimuth();
    }

}
