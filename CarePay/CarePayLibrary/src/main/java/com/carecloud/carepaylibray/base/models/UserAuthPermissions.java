package com.carecloud.carepaylibray.base.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 1/17/18
 */

public class UserAuthPermissions {

    @SerializedName("accept_appointment_request")
    public boolean canAcceptAppointmentRequest = false;

    @SerializedName("cancel_appointment")
    public boolean canCancelAppointment = false;

    @SerializedName("schedule_appointment")
    public boolean canScheduleAppointment = false;

    @SerializedName("make_payment")
    public boolean canMakePayment = false;

    @SerializedName("make_refund")
    public boolean canMakeRefund = false;

    @SerializedName("add_charges")
    public boolean canAddCharges = false;

}
