package com.carecloud.carepay.patient.myhealth.dtos;

import com.carecloud.carepay.patient.base.PagingDto;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pjohnson on 17/07/17.
 */
public class MyHealthMedicationsDto extends PagingDto {

    List<MedicationDto> medications = new ArrayList<>();

    public List<MedicationDto> getMedications() {
        return medications;
    }

    public void setMedications(List<MedicationDto> medications) {
        this.medications = medications;
    }
}
