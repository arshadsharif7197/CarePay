package com.carecloud.carepay.patient.myhealth.dtos;

import com.carecloud.carepay.patient.base.PagingDto;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pjohnson on 17/07/17.
 */
public class MyHealthAllergiesDto extends PagingDto {

    private List<AllergyDto> allergies = new ArrayList<>();

    public List<AllergyDto> getAllergies() {
        return allergies;
    }

    public void setAllergies(List<AllergyDto> allergies) {
        this.allergies = allergies;
    }
}
