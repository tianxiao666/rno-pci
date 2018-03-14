package com.hgicreate.rno.lte.pciafp.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
public class JobProfile implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long jobId;
    private Date submitTime = null;
    private Date launchTime = null;
    private Date finishTime = null;
    private Integer priority = 1;
    private String jobType;
    private String account;
    private String jobName;
    private String jobStateStr;
    private String description;
    private String status = "N";

    public JobProfile(Long jobId) {
        this.jobId = jobId;
    }

    public boolean canSubmit() {
        return account != null && jobType != null && jobName != null;
    }
}
