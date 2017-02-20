package com.carecloud.carepaylibray.appointments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by arpit_jain1 on 11/7/2016.
 * Model for Data
 */
public class DataDTO {

    @SerializedName("cancellation_comments")
    @Expose
    private QueryString cancellationComments = new QueryString();
    @SerializedName("appointment_id")
    @Expose
    private QueryString appointmentId = new QueryString();

    /**
     * 
     * @return
     *     The cancellationComments
     */
    public QueryString getCancellationComments() {
        return cancellationComments;
    }

    /**
     * 
     * @param cancellationComments
     *     The cancellation_comments
     */
    public void setCancellationComments(QueryString cancellationComments) {
        this.cancellationComments = cancellationComments;
    }

    /**
     *
     * @return
     *     The appointmentId
     */
    public QueryString getAppointmentId() {
        return appointmentId;
    }

    /**
     *
     * @param appointmentId
     *     The appointment_id
     */
    public void setAppointmentId(QueryString appointmentId) {
        this.appointmentId = appointmentId;
    }

}
