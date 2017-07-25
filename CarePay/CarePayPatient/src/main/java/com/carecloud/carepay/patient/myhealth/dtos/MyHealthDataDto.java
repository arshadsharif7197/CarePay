package com.carecloud.carepay.patient.myhealth.dtos;

import com.google.gson.annotations.Expose;

/**
 * @author pjohnson on 17/07/17.
 */
public class MyHealthDataDto {

    @Expose
    private MyHealthMedicationsDto medications = new MyHealthMedicationsDto();
    @Expose
    private MyHealthLabsDto labs = new MyHealthLabsDto();
    @Expose
    private MyHealthAllergiesDto allergies = new MyHealthAllergiesDto();
    @Expose
    private MyHealthAssertionsDto assertions = new MyHealthAssertionsDto();
    @Expose
    private MyHealthProvidersDto providers = new MyHealthProvidersDto();
    @Expose
    private MyHealthPatientDto patient = new MyHealthPatientDto();


    public MyHealthMedicationsDto getMedications() {
        return medications;
    }

    public void setMedications(MyHealthMedicationsDto medications) {
        this.medications = medications;
    }

    public MyHealthLabsDto getLabs() {
        return labs;
    }

    public void setLabs(MyHealthLabsDto labs) {
        this.labs = labs;
    }

    public MyHealthAllergiesDto getAllergies() {
        return allergies;
    }

    public void setAllergies(MyHealthAllergiesDto allergies) {
        this.allergies = allergies;
    }

    public MyHealthAssertionsDto getAssertions() {
        return assertions;
    }

    public void setAssertions(MyHealthAssertionsDto assertions) {
        this.assertions = assertions;
    }

    public MyHealthProvidersDto getProviders() {
        return providers;
    }

    public void setProviders(MyHealthProvidersDto providers) {
        this.providers = providers;
    }

    public MyHealthPatientDto getPatient() {
        return patient;
    }

    public void setPatient(MyHealthPatientDto patient) {
        this.patient = patient;
    }
}
