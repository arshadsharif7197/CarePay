package com.carecloud.carepay.patient.myhealth.dtos;

import com.carecloud.carepaylibray.base.models.PagingDto;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pjohnson on 17/07/17.
 */
public class MyHealthMedicationsDto extends PagingDto {

    @Expose
    @SerializedName("medications")
    private List<MedicationDto> medications = new ArrayList<>();

    public List<MedicationDto> getMedications() {
        return medications;
    }

    public void setMedications(List<MedicationDto> medications) {
        this.medications = medications;
    }
}
