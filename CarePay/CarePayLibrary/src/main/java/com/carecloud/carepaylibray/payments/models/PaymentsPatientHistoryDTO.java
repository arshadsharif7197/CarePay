package com.carecloud.carepaylibray.payments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by jorge on 05/01/17.
 */

public class PaymentsPatientHistoryDTO {
    @SerializedName("charges")
    @Expose
    private PaymentsPatientChargesDTO paymentsPatientCharges = new PaymentsPatientChargesDTO();

    public PaymentsPatientChargesDTO getPaymentsPatientCharges() {
        return paymentsPatientCharges;
    }

    public void setPaymentsPatientCharges(PaymentsPatientChargesDTO paymentsPatientCharges) {
        this.paymentsPatientCharges = paymentsPatientCharges;
    }
}
