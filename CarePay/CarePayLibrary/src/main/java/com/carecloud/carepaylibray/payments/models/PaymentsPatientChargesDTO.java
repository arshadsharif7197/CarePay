package com.carecloud.carepaylibray.payments.models;

import com.carecloud.carepaylibray.appointments.models.AppointmentChargeDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jorge on 05/01/17.
 */

public class PaymentsPatientChargesDTO {
    @SerializedName("payload")
    @Expose
    List<AppointmentChargeDTO> charges = new ArrayList<>();

    public List<AppointmentChargeDTO> getCharges() {
        return charges;
    }

    public void setCharges(List<AppointmentChargeDTO> charges) {
        this.charges = charges;
    }
}
