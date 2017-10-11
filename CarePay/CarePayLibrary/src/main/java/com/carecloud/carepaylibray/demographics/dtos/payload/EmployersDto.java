package com.carecloud.carepaylibray.demographics.dtos.payload;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pjohnson on 6/10/17.
 */

public class EmployersDto {

    @Expose
    @SerializedName("data")
    private List<EmployerWrapperDto> employersData = new ArrayList<>();

    public List<EmployerWrapperDto> getEmployersData() {
        return employersData;
    }

    public void setEmployersData(List<EmployerWrapperDto> employersData) {
        this.employersData = employersData;
    }
}
