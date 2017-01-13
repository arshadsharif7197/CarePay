
package com.carecloud.carepaylibray.payments.models;

import java.util.ArrayList;
import java.util.List;

import com.carecloud.carepaylibray.appointments.models.AppointmentChargeDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PatiencePayloadDTO {

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("amount")
    @Expose
    private Double amount;
    @SerializedName("details")
    @Expose
    private List<AppointmentChargeDTO> details = new ArrayList<>();

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public List<AppointmentChargeDTO> getDetails() {
        return details;
    }

    public void setDetails(List<AppointmentChargeDTO> details) {
        this.details = details;
    }

}
