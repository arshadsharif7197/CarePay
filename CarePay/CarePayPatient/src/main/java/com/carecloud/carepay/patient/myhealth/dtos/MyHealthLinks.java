package com.carecloud.carepay.patient.myhealth.dtos;

import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepaylibray.base.dtos.LandingLinks;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author pjohnson on 9/08/17.
 */

public class MyHealthLinks extends LandingLinks {

    @Expose
    @SerializedName("medications")
    private TransitionDTO medications = new TransitionDTO();
    @Expose
    @SerializedName("allergies")
    private TransitionDTO allergies = new TransitionDTO();
    @Expose
    @SerializedName("conditions")
    private TransitionDTO conditions = new TransitionDTO();
    @Expose
    @SerializedName("labs")
    private TransitionDTO labs = new TransitionDTO();
    @Expose
    @SerializedName("labs_pdf")
    private TransitionDTO labsPdf = new TransitionDTO();
    @Expose
    @SerializedName("education_material")
    private TransitionDTO educationMaterial = new TransitionDTO();
    @Expose
    @SerializedName("careteam")
    private TransitionDTO medicalRecord = new TransitionDTO();
    @Expose
    @SerializedName("visit_summary")
    private TransitionDTO visitSummary = new TransitionDTO();
    @Expose
    @SerializedName("visit_summary_status")
    private TransitionDTO visitSummaryStatus = new TransitionDTO();

    public TransitionDTO getMedications() {
        return medications;
    }

    public void setMedications(TransitionDTO medications) {
        this.medications = medications;
    }

    public TransitionDTO getAllergies() {
        return allergies;
    }

    public void setAllergies(TransitionDTO allergies) {
        this.allergies = allergies;
    }

    public TransitionDTO getConditions() {
        return conditions;
    }

    public void setConditions(TransitionDTO conditions) {
        this.conditions = conditions;
    }

    public TransitionDTO getLabs() {
        return labs;
    }

    public void setLabs(TransitionDTO labs) {
        this.labs = labs;
    }

    public TransitionDTO getLabsPdf() {
        return labsPdf;
    }

    public void setLabsPdf(TransitionDTO labsPdf) {
        this.labsPdf = labsPdf;
    }

    public TransitionDTO getEducationMaterial() {
        return educationMaterial;
    }

    public void setEducationMaterial(TransitionDTO educationMaterial) {
        this.educationMaterial = educationMaterial;
    }

    public TransitionDTO getMedicalRecord() {
        return medicalRecord;
    }

    public void setMedicalRecord(TransitionDTO medicalRecord) {
        this.medicalRecord = medicalRecord;
    }

    public TransitionDTO getVisitSummary() {
        return visitSummary;
    }

    public void setVisitSummary(TransitionDTO visitSummary) {
        this.visitSummary = visitSummary;
    }

    public TransitionDTO getVisitSummaryStatus() {
        return visitSummaryStatus;
    }

    public void setVisitSummaryStatus(TransitionDTO visitSummaryStatus) {
        this.visitSummaryStatus = visitSummaryStatus;
    }
}
