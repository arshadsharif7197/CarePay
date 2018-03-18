
package com.carecloud.carepaylibray.payments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class PatienceBalancePayloadDTO {

    @SerializedName("summary_balance")
    @Expose
    private List<PaymentPatientBalancesPayloadDTO> summaryBalance = new ArrayList<>();

    public List<PaymentPatientBalancesPayloadDTO> getSummaryBalance() {
        return summaryBalance;
    }

    public void setSummaryBalance(List<PaymentPatientBalancesPayloadDTO> summaryBalance) {
        this.summaryBalance = summaryBalance;
    }

}
