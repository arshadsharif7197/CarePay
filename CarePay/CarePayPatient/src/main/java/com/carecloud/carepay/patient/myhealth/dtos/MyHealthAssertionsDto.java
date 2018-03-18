package com.carecloud.carepay.patient.myhealth.dtos;

import com.carecloud.carepaylibray.base.models.PagingDto;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pjohnson on 17/07/17.
 */
public class MyHealthAssertionsDto extends PagingDto {

    @Expose
    @SerializedName("assertions")
    private List<AssertionDto> assertions = new ArrayList<>();

    public List<AssertionDto> getAssertions() {
        return assertions;
    }

    public void setAssertions(List<AssertionDto> assertions) {
        this.assertions = assertions;
    }
}
