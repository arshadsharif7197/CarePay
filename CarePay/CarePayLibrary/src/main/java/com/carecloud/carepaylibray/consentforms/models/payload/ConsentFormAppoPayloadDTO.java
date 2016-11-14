package com.carecloud.carepaylibray.consentforms.models.payload;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Rahul on 11/13/16.
 */

public class ConsentFormAppoPayloadDTO {
    @SerializedName("patient")
    @Expose
    private ConsentFormAppoPatientDTO appointmentPatient;


    @SerializedName("provider")
    @Expose
    private AppoPayloadProvider appoPayloadProvider;

    public ConsentFormAppoPatientDTO getAppointmentPatient() {
        return appointmentPatient;
    }

    public void setAppointmentPatient(ConsentFormAppoPatientDTO appointmentPatient) {
        this.appointmentPatient = appointmentPatient;
    }

    public AppoPayloadProvider getAppoPayloadProvider() {
        return appoPayloadProvider;
    }

    public void setAppoPayloadProvider(AppoPayloadProvider appoPayloadProvider) {
        this.appoPayloadProvider = appoPayloadProvider;
    }
}
