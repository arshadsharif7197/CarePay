package com.carecloud.carepaylibray.demographics.dtos.payload;

/**
 * @author pjohnson on 6/10/17.
 */

public class EmployerWrapperDto {

    private EmployerDto employer = new EmployerDto();

    public EmployerDto getEmployer() {
        return employer;
    }

    public void setEmployer(EmployerDto employer) {
        this.employer = employer;
    }
}
