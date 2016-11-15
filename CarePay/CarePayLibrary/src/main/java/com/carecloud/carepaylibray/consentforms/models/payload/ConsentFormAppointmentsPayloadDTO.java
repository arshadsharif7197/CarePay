package com.carecloud.carepaylibray.consentforms.models.payload;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Rahul on 11/13/16.
 */

public class ConsentFormAppointmentsPayloadDTO {

    @SerializedName("metadata")
    @Expose
    private ConsentFormAppoMetadataDTO appointmentMetadata;
    @SerializedName("payload")
    @Expose
    private ConsentFormAppoPayloadDTO appointmentPayload;

    public ConsentFormAppoPayloadDTO getAppointmentPayload() {
        return appointmentPayload;
    }

    public void setAppointmentPayload(ConsentFormAppoPayloadDTO appointmentPayload) {
        this.appointmentPayload = appointmentPayload;
    }

    public ConsentFormAppoMetadataDTO getAppointmentMetadata() {
        return appointmentMetadata;
    }

    public void setAppointmentMetadata(ConsentFormAppoMetadataDTO appointmentMetadata) {
        this.appointmentMetadata = appointmentMetadata;
    }


}
