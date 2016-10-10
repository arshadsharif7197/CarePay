
package com.carecloud.carepaylibray.payment.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PaymentsPracticeQueryStringModel {

    @SerializedName("paymentsPracticeModel")
    @Expose
    private PaymentsPracticeModel paymentsPracticeModel;
    @SerializedName("business_entity_id")
    @Expose
    private PaymentsBusinessEntityIdModel paymentsBusinessEntityIdModel;
    @SerializedName("appointment_id")
    @Expose
    private PaymentsAppointmentIdModel paymentsAppointmentIdModel;

    /**
     * 
     * @return
     *     The paymentsPracticeModel
     */
    public PaymentsPracticeModel getPaymentsPracticeModel() {
        return paymentsPracticeModel;
    }

    /**
     * 
     * @param paymentsPracticeModel
     *     The paymentsPracticeModel
     */
    public void setPaymentsPracticeModel(PaymentsPracticeModel paymentsPracticeModel) {
        this.paymentsPracticeModel = paymentsPracticeModel;
    }

    /**
     * 
     * @return
     *     The paymentsBusinessEntityIdModel
     */
    public PaymentsBusinessEntityIdModel getPaymentsBusinessEntityIdModel() {
        return paymentsBusinessEntityIdModel;
    }

    /**
     * 
     * @param paymentsBusinessEntityIdModel
     *     The business_entity_id
     */
    public void setPaymentsBusinessEntityIdModel(PaymentsBusinessEntityIdModel paymentsBusinessEntityIdModel) {
        this.paymentsBusinessEntityIdModel = paymentsBusinessEntityIdModel;
    }

    /**
     * 
     * @return
     *     The paymentsAppointmentIdModel
     */
    public PaymentsAppointmentIdModel getPaymentsAppointmentIdModel() {
        return paymentsAppointmentIdModel;
    }

    /**
     * 
     * @param paymentsAppointmentIdModel
     *     The appointment_id
     */
    public void setPaymentsAppointmentIdModel(PaymentsAppointmentIdModel paymentsAppointmentIdModel) {
        this.paymentsAppointmentIdModel = paymentsAppointmentIdModel;
    }

}
