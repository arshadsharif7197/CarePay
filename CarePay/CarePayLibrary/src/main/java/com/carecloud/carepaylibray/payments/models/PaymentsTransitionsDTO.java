
package com.carecloud.carepaylibray.payments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PaymentsTransitionsDTO {

    @SerializedName("confirm_payment")
    @Expose
    private PaymentsConfirmDTO paymentsConfirm;
    @SerializedName("post_payment")
    @Expose
    private PaymentsPostPayDTO paymentsPostPay;
    @SerializedName("post_credit_cards")
    @Expose
    private PaymentsPostCreditCardsDTO paymentsPostCreditCards;
    @SerializedName("delete_credit_cards")
    @Expose
    private PaymentsDeleteCreditCardsDTO paymentsDeleteCreditCards;
    @SerializedName("checkin_appointment")
    @Expose
    private PaymentsCheckinAppointmentDTO paymentsCheckinAppointment;

    /**
     * 
     * @return
     *     The paymentsConfirm
     */
    public PaymentsConfirmDTO getPaymentsConfirm() {
        return paymentsConfirm;
    }

    /**
     * 
     * @param paymentsConfirm
     *     The confirm_payment
     */
    public void setPaymentsConfirm(PaymentsConfirmDTO paymentsConfirm) {
        this.paymentsConfirm = paymentsConfirm;
    }

    /**
     * 
     * @return
     *     The paymentsPostPay
     */
    public PaymentsPostPayDTO getPaymentsPostPay() {
        return paymentsPostPay;
    }

    /**
     * 
     * @param paymentsPostPay
     *     The post_payment
     */
    public void setPaymentsPostPay(PaymentsPostPayDTO paymentsPostPay) {
        this.paymentsPostPay = paymentsPostPay;
    }

    /**
     * 
     * @return
     *     The paymentsPostCreditCards
     */
    public PaymentsPostCreditCardsDTO getPaymentsPostCreditCards() {
        return paymentsPostCreditCards;
    }

    /**
     * 
     * @param paymentsPostCreditCards
     *     The post_credit_cards
     */
    public void setPaymentsPostCreditCards(PaymentsPostCreditCardsDTO paymentsPostCreditCards) {
        this.paymentsPostCreditCards = paymentsPostCreditCards;
    }

    /**
     * 
     * @return
     *     The paymentsDeleteCreditCards
     */
    public PaymentsDeleteCreditCardsDTO getPaymentsDeleteCreditCards() {
        return paymentsDeleteCreditCards;
    }

    /**
     * 
     * @param paymentsDeleteCreditCards
     *     The delete_credit_cards
     */
    public void setPaymentsDeleteCreditCards(PaymentsDeleteCreditCardsDTO paymentsDeleteCreditCards) {
        this.paymentsDeleteCreditCards = paymentsDeleteCreditCards;
    }

    /**
     * 
     * @return
     *     The paymentsCheckinAppointment
     */
    public PaymentsCheckinAppointmentDTO getPaymentsCheckinAppointment() {
        return paymentsCheckinAppointment;
    }

    /**
     * 
     * @param paymentsCheckinAppointment
     *     The checkin_appointment
     */
    public void setPaymentsCheckinAppointment(PaymentsCheckinAppointmentDTO paymentsCheckinAppointment) {
        this.paymentsCheckinAppointment = paymentsCheckinAppointment;
    }

}
