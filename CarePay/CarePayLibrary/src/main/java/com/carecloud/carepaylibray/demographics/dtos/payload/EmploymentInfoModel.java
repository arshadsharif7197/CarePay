package com.carecloud.carepaylibray.demographics.dtos.payload;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 11/4/17
 */

public class EmploymentInfoModel {

    @SerializedName("employment_status")
    private String employmentStatus;

    @SerializedName("employer")
    private EmployerDto employerDto = new EmployerDto();


    public String getEmploymentStatus() {
        return employmentStatus;
    }

    public void setEmploymentStatus(String employmentStatus) {
        this.employmentStatus = employmentStatus;
    }

    public EmployerDto getEmployerDto() {
        return employerDto;
    }

    public void setEmployerDto(EmployerDto employerDto) {
        this.employerDto = employerDto;
    }
}
