package com.carecloud.carepay.patient.myhealth.dtos;

import com.carecloud.carepaylibray.base.models.PagingDto;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pjohnson on 17/07/17.
 */
public class MyHealthAssertionsDto extends PagingDto {

    private List<AssertionDto> assertions = new ArrayList<>();

    public List<AssertionDto> getAssertions() {
        return assertions;
    }

    public void setAssertions(List<AssertionDto> assertions) {
        this.assertions = assertions;
    }
}
