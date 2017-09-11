package com.carecloud.carepaylibray.payments.models.postmodel;

import com.carecloud.carepaylibray.appointments.models.ScheduleAppointmentRequestDTO;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 8/29/17
 */

public class IntegratedPaymentMetadata {

    @SerializedName("appointment_id")
    private String appointmentId;

    @SerializedName("appointment")
    private ScheduleAppointmentRequestDTO.Appointment appointmentRequestDTO;

    @SerializedName("cancellation_reason_id")
    private String cancellationReasonId;

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public ScheduleAppointmentRequestDTO.Appointment getAppointmentRequestDTO() {
        return appointmentRequestDTO;
    }

    public void setAppointmentRequestDTO(ScheduleAppointmentRequestDTO.Appointment appointmentRequestDTO) {
        this.appointmentRequestDTO = appointmentRequestDTO;
    }

    public void setCancellationReasonId(String cancellationReasonId) {
        this.cancellationReasonId = cancellationReasonId;
    }

    public String getCancellationReasonId() {
        return cancellationReasonId;
    }
}
