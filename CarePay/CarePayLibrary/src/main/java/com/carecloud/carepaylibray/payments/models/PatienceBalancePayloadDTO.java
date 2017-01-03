
package com.carecloud.carepaylibray.payments.models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PatienceBalancePayloadDTO {

    @SerializedName("summary_balance")
    @Expose
    private List<PaymentPatientBalancesPayloadDTO> summaryBalance = null;

    public List<PaymentPatientBalancesPayloadDTO> getSummaryBalance() {
        return summaryBalance;
    }

    public void setSummaryBalance(List<PaymentPatientBalancesPayloadDTO> summaryBalance) {
        this.summaryBalance = summaryBalance;
    }

}
