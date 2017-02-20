package com.carecloud.carepaylibray.appointments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CancellationReasonDTO {

    @SerializedName("appointment_cancellation_reason")
    @Expose
    private AppointmentCancellationReasonDTO appointmentCancellationReason = new AppointmentCancellationReasonDTO();

    /**
     * @return The appointmentCancellationReason
     */
    public AppointmentCancellationReasonDTO getAppointmentCancellationReason() {
        return appointmentCancellationReason;
    }

    /**
     * @param appointmentCancellationReason The appointment_cancellation_reason
     */
    public void setAppointmentCancellationReason(AppointmentCancellationReasonDTO appointmentCancellationReason) {
        this.appointmentCancellationReason = appointmentCancellationReason;
    }

}
