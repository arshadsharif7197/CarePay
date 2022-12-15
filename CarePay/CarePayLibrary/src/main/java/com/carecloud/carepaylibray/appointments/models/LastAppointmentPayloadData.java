package com.carecloud.carepaylibray.appointments.models;

import com.carecloud.carepaylibray.intake.models.AppointmentPayloadModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LastAppointmentPayloadData {
    @SerializedName("payload")
    @Expose
    private AppointmentPayloadModel appointmentPayloadModel;
}
