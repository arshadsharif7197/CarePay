package com.carecloud.carepayclover.models;

/**
 * Created by Jahirul Bhuiyan on 9/28/2016.
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
public class AppointmentsCheckedInPayloadAppointmentModel {

    @SerializedName("payload")
    @Expose
    private CheckedInAppointmentPayloadModel payload;

    /**
     *
     * @return
     * The payload
     */
    public CheckedInAppointmentPayloadModel getPayload() {
        return payload;
    }

    /**
     *
     * @param payload
     * The payload
     */
    public void setPayload(CheckedInAppointmentPayloadModel payload) {
        this.payload = payload;
    }

}
