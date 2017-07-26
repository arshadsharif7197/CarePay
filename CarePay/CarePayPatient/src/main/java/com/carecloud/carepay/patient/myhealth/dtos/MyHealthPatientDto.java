package com.carecloud.carepay.patient.myhealth.dtos;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pjohnson on 17/07/17.
 */
public class MyHealthPatientDto {

    List<PatientDto> patients = new ArrayList<>();

    public List<PatientDto> getPatients() {
        return patients;
    }

    public void setPatients(List<PatientDto> patients) {
        this.patients = patients;
    }
}
