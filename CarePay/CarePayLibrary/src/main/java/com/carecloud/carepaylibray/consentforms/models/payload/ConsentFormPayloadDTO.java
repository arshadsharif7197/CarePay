package com.carecloud.carepaylibray.consentforms.models.payload;

import com.carecloud.carepaylibray.demographics.dtos.payload.ConsentFormUserResponseDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rahul on 11/13/16.
 */

public class ConsentFormPayloadDTO {


    @SerializedName("demographics")
    @Expose
    private ConseFormDemoagraphicsPayloadDTO demographics = new ConseFormDemoagraphicsPayloadDTO();

    @SerializedName("appointments")
    @Expose
    private List<ConsentFormAppointmentsPayloadDTO> consentFormAppointmentPayload = new ArrayList<>();

    @Expose
    @SerializedName("consent_forms_user_response")
    private List<ConsentFormUserResponseDTO> responses = new ArrayList<>();

    public ConseFormDemoagraphicsPayloadDTO getDemographics() {
        return demographics;
    }

    public void setDemographics(ConseFormDemoagraphicsPayloadDTO demographics) {
        this.demographics = demographics;
    }

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
}
