package com.carecloud.carepaylibray.consentforms.models.payload;

import com.carecloud.carepaylibray.intake.models.AppointmentMetadataModel;
import com.carecloud.carepaylibray.intake.models.PayloadAppointmentModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Rahul on 11/13/16.
 */

public class ConsentFormAppointmentsPayloadDTO {

    @SerializedName("metadata")
    @Expose
    private AppointmentMetadataModel appointmentMetadata = new AppointmentMetadataModel();
    @SerializedName("payload")
    @Expose
    private PayloadAppointmentModel appointmentPayload = new PayloadAppointmentModel();

    public PayloadAppointmentModel getAppointmentPayload() {
        return appointmentPayload;
    }

    public void setAppointmentPayload(PayloadAppointmentModel appointmentPayload) {
        this.appointmentPayload = appointmentPayload;
    }

    public AppointmentMetadataModel getAppointmentMetadata() {
        return appointmentMetadata;
    }

    public void setAppointmentMetadata(AppointmentMetadataModel appointmentMetadata) {
        this.appointmentMetadata = appointmentMetadata;
    }


}
