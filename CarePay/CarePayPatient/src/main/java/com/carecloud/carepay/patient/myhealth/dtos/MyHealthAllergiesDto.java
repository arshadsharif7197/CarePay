package com.carecloud.carepay.patient.myhealth.dtos;

import com.carecloud.carepaylibray.base.models.PagingDto;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pjohnson on 17/07/17.
 */
public class MyHealthAllergiesDto extends PagingDto {

    @Expose
    @SerializedName("allergies")
    private List<AllergyDto> allergies = new ArrayList<>();

    public List<AllergyDto> getAllergies() {
        return allergies;
    }

    public void setAllergies(List<AllergyDto> allergies) {
        this.allergies = allergies;
    }
}
