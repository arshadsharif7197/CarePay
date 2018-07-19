package com.carecloud.carepaylibray.appointments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by harshal_patil on 2/14/2017.
 */
public class AppointmentsRequestsDTO implements Serializable {
    @SerializedName("automatically_approve_requests")
    @Expose
    private boolean automaticallyApproveRequests;

    public boolean getAutomaticallyApproveRequests() {
        return automaticallyApproveRequests;
    }

    public void setAutomaticallyApproveRequests(boolean automaticallyApproveRequests) {
        this.automaticallyApproveRequests = automaticallyApproveRequests;
    }
}
