package com.carecloud.carepayclover.models;

/**
 * Created by Jahirul Bhuiyan on 9/28/2016.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
public class AppointmentsCheckedInModel {


    @SerializedName("payload")
    @Expose
    private AppointmentsCheckedInPayloadModel payload;
    @SerializedName("state")
    @Expose
    private String state;


    /**
     *
     * @return
     * The payload
     */
    public AppointmentsCheckedInPayloadModel getPayload() {
        return payload;
    }

    /**
     *
     * @param payload
     * The payload
     */
    public void setPayload(AppointmentsCheckedInPayloadModel payload) {
        this.payload = payload;
    }

    /**
     *
     * @return
     * The state
     */
    public String getState() {
        return state;
    }

    /**
     *
     * @param state
     * The state
     */
    public void setState(String state) {
        this.state = state;
    }
}
