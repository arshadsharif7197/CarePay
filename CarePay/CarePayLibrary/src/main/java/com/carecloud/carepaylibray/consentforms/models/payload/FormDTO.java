package com.carecloud.carepaylibray.consentforms.models.payload;

import com.carecloud.carepaylibray.consentforms.models.datamodels.practiceforms.PracticeForm;
import com.carecloud.carepaylibray.demographics.dtos.payload.ConsentFormUserResponseDTO;
import com.carecloud.carepaylibray.payments.models.PendingBalanceMetadataDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pjohnson on 7/03/18.
 */

public class FormDTO {

    @Expose
    @SerializedName("metadata")
    private PendingBalanceMetadataDTO metadata = new PendingBalanceMetadataDTO();

    @Expose
    @SerializedName("practice_forms")
    private List<PracticeForm> practiceForms = new ArrayList<>();

    @Expose
    @SerializedName("patient_forms_response")
    private List<ConsentFormUserResponseDTO> patientFormsResponses = new ArrayList<>();

    @Expose
    @SerializedName("patient_forms_filled")
    private List<ConsentFormUserResponseDTO> patientFormsFilled = new ArrayList<>();

    @Expose
    @SerializedName("pending_forms")
    private List<String> pendingForms = new ArrayList<>();

    public PendingBalanceMetadataDTO getMetadata() {
        return metadata;
    }

    public void setMetadata(PendingBalanceMetadataDTO metadata) {
        this.metadata = metadata;
    }

    public List<PracticeForm> getPracticeForms() {
        return practiceForms;
    }

    public void setPracticeForms(List<PracticeForm> practiceForms) {
        this.practiceForms = practiceForms;
    }

    public List<ConsentFormUserResponseDTO> getPatientFormsResponses() {
        return patientFormsResponses;
    }

    public void setPatientFormsResponses(List<ConsentFormUserResponseDTO> patientFormsResponses) {
        this.patientFormsResponses = patientFormsResponses;
    }

    public List<ConsentFormUserResponseDTO> getPatientFormsFilled() {
        return patientFormsFilled;
    }

    public void setPatientFormsFilled(List<ConsentFormUserResponseDTO> patientFormsFilled) {
        this.patientFormsFilled = patientFormsFilled;
    }

    public List<String> getPendingForms() {
        return pendingForms;
    }

    public void setPendingForms(List<String> pendingForms) {
        this.pendingForms = pendingForms;
    }
}
