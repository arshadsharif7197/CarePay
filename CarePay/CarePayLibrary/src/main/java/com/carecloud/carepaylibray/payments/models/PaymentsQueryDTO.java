
package com.carecloud.carepaylibray.payments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PaymentsQueryDTO {

    @SerializedName("start_date")
    @Expose
    private PaymentsStartDateDTO paymentsStartDate = new PaymentsStartDateDTO();
    @SerializedName("end_date")
    @Expose
    private PaymentsEndDateDTO paymentsEndDate = new PaymentsEndDateDTO();

    /**
     * 
     * @return
     *     The paymentsStartDate
     */
    public PaymentsStartDateDTO getPaymentsStartDate() {
        return paymentsStartDate;
    }

    /**
     * 
     * @param paymentsStartDate
     *     The start_date
     */
    public void setPaymentsStartDate(PaymentsStartDateDTO paymentsStartDate) {
        this.paymentsStartDate = paymentsStartDate;
    }

    /**
     * 
     * @return
     *     The paymentsEndDate
     */
    public PaymentsEndDateDTO getPaymentsEndDate() {
        return paymentsEndDate;
    }

    /**
     * 
     * @param paymentsEndDate
     *     The end_date
     */
    public void setPaymentsEndDate(PaymentsEndDateDTO paymentsEndDate) {
        this.paymentsEndDate = paymentsEndDate;
    }

}
