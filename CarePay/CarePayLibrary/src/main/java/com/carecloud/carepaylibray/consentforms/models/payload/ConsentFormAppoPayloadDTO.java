package com.carecloud.carepaylibray.consentforms.models.payload;

import com.carecloud.carepaylibray.base.models.PatientModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Rahul on 11/13/16.
 */

public class ConsentFormAppoPayloadDTO {
    @SerializedName("patient")
    @Expose
    private PatientModel appointmentPatient = new PatientModel();


    @SerializedName("provider")
    @Expose
    private AppoPayloadProvider appoPayloadProvider = new AppoPayloadProvider();

    public PatientModel getAppointmentPatient() {
        return appointmentPatient;
    }

    public void setAppointmentPatient(PatientModel appointmentPatient) {
        this.appointmentPatient = appointmentPatient;
    }

    public AppoPayloadProvider getAppoPayloadProvider() {
        return appoPayloadProvider;
    }

    public void setAppoPayloadProvider(AppoPayloadProvider appoPayloadProvider) {
        this.appoPayloadProvider = appoPayloadProvider;
    }
}
