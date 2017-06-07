package com.carecloud.carepaylibray.consentforms.models.payload;

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
    
}
