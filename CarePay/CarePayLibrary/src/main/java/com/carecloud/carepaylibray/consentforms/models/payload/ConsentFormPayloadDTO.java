package com.carecloud.carepaylibray.consentforms.models.payload;

import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.ConsentFormUserResponseDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rahul on 11/13/16.
 */

public class ConsentFormPayloadDTO {

    @SerializedName("appointments")
    @Expose
    private List<ConsentFormAppointmentsPayloadDTO> consentFormAppointmentPayload = new ArrayList<>();

    @Expose
    @SerializedName("patient_forms_response")
    private List<ConsentFormUserResponseDTO> responses = new ArrayList<>();

    @Expose
    @SerializedName("forms")
    private List<FormDTO> forms = new ArrayList<>();

    @Expose
    @SerializedName("practice_information")
    private List<UserPracticeDTO> practicesInformation;

    public List<ConsentFormAppointmentsPayloadDTO> getConsentFormAppointmentPayload() {
        return consentFormAppointmentPayload;
    }

    public void setConsentFormAppointmentPayload(List<ConsentFormAppointmentsPayloadDTO> consentFormAppointmentPayload) {
        this.consentFormAppointmentPayload = consentFormAppointmentPayload;
    }

    public List<ConsentFormUserResponseDTO> getResponses() {
        return responses;
    }

    public void setResponses(List<ConsentFormUserResponseDTO> responses) {
        this.responses = responses;
    }

    public List<FormDTO> getForms() {
        return forms;
    }

    public void setForms(List<FormDTO> forms) {
        this.forms = forms;
    }

    public List<UserPracticeDTO> getPracticesInformation() {
        return practicesInformation;
    }

    public void setPracticesInformation(List<UserPracticeDTO> practicesInformation) {
        this.practicesInformation = practicesInformation;
    }
}
