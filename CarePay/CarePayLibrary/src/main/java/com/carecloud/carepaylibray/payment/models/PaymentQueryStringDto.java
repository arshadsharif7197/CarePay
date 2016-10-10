
package com.carecloud.carepaylibray.payment.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PaymentQueryStringDto {

    @SerializedName("start_date")
    @Expose
    private PaymentsStartDateModel paymentsStartDateModel;
    @SerializedName("end_date")
    @Expose
    private PaymentsEndDateModel paymentsEndDateModel;

    /**
     * 
     * @return
     *     The paymentsStartDateModel
     */
    public PaymentsStartDateModel getPaymentsStartDateModel() {
        return paymentsStartDateModel;
    }

    /**
     * 
     * @param paymentsStartDateModel
     *     The start_date
     */
    public void setPaymentsStartDateModel(PaymentsStartDateModel paymentsStartDateModel) {
        this.paymentsStartDateModel = paymentsStartDateModel;
    }

    /**
     * 
     * @return
     *     The paymentsEndDateModel
     */
    public PaymentsEndDateModel getPaymentsEndDateModel() {
        return paymentsEndDateModel;
    }

    /**
     * 
     * @param paymentsEndDateModel
     *     The end_date
     */
    public void setPaymentsEndDateModel(PaymentsEndDateModel paymentsEndDateModel) {
        this.paymentsEndDateModel = paymentsEndDateModel;
    }

}
