
package com.carecloud.carepaylibray.payments.models;

import com.carecloud.carepaylibray.base.models.BaseTransitionsPropertyModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PaymentsPracticeQueryDTO {

    @SerializedName("practice")
    @Expose
    private PaymentsPracticeDTO paymentsPractice = new PaymentsPracticeDTO();
    @SerializedName("business_entity_id")
    @Expose
    private BaseTransitionsPropertyModel paymentsBusinessEntityId = new BaseTransitionsPropertyModel();
    @SerializedName("appointment_id")
    @Expose
    private BaseTransitionsPropertyModel paymentsAppointmentId = new BaseTransitionsPropertyModel();

    /**
     * 
     * @return
     *     The paymentsPracticeDTO
     */
    public PaymentsPracticeDTO getPaymentsPractice() {
        return paymentsPractice;
    }

    /**
     * 
     * @param paymentsPractice
     *     The paymentsPractice
     */
    public void setPaymentsPracticeDTO(PaymentsPracticeDTO paymentsPractice) {
        this.paymentsPractice = paymentsPractice;
    }

    /**
     * 
     * @return
     *     The paymentsBusinessEntityId
     */
    public BaseTransitionsPropertyModel getPaymentsBusinessEntityId() {
        return paymentsBusinessEntityId;
    }

    /**
     * 
     * @param paymentsBusinessEntityId
     *     The business_entity_id
     */
    public void setPaymentsBusinessEntityId(BaseTransitionsPropertyModel paymentsBusinessEntityId) {
        this.paymentsBusinessEntityId = paymentsBusinessEntityId;
    }

    /**
     * 
     * @return
     *     The paymentsAppointmentId
     */
    public BaseTransitionsPropertyModel getPaymentsAppointmentId() {
        return paymentsAppointmentId;
    }

    /**
     * 
     * @param paymentsAppointmentId
     *     The appointment_id
     */
    public void setPaymentsAppointmentId(BaseTransitionsPropertyModel paymentsAppointmentId) {
        this.paymentsAppointmentId = paymentsAppointmentId;
    }

}
