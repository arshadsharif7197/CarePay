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
    private Boolean automaticallyApproveRequests;

    public Boolean getAutomaticallyApproveRequests() {
        return automaticallyApproveRequests;
    }

    public void setAutomaticallyApproveRequests(Boolean automaticallyApproveRequests) {
        this.automaticallyApproveRequests = automaticallyApproveRequests;
    }
}
