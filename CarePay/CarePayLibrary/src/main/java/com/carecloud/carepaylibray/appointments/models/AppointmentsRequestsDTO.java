package com.carecloud.carepaylibray.appointments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by harshal_patil on 2/14/2017.
 * Updated by Muhammad Noman on 08/20/2022.
 */
public class AppointmentsRequestsDTO implements Serializable {
    @SerializedName("automatically_approve_requests")
    @Expose
    private boolean automaticallyApproveRequests;
    @SerializedName("restriction_period")
    @Expose
    private double restrictionPeriod;

    public boolean getAutomaticallyApproveRequests() {
        return automaticallyApproveRequests;
    }

    public void setAutomaticallyApproveRequests(boolean automaticallyApproveRequests) {
        this.automaticallyApproveRequests = automaticallyApproveRequests;
    }

    public double getRestrictionPeriod() {
        return restrictionPeriod;
    }

    public void setRestrictionPeriod(double restrictionPeriod) {
        this.restrictionPeriod = restrictionPeriod;
    }
}
