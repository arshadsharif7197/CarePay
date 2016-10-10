
package com.carecloud.carepaylibray.payment.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PaymentsTransitionsModel {

    @SerializedName("confirm_payment")
    @Expose
    private PaymentsConfirmModel paymentsConfirmModel;
    @SerializedName("post_payment")
    @Expose
    private PaymentsPostPayModel paymentsPostPayModel;
    @SerializedName("post_credit_cards")
    @Expose
    private PaymentsPostCreditCardsModel paymentsPostCreditCardsModel;
    @SerializedName("delete_credit_cards")
    @Expose
    private PaymentsDeleteCreditCardsModel paymentsDeleteCreditCardsModel;
    @SerializedName("checkin_appointment")
    @Expose
    private PaymentsCheckinAppointmentModel paymentsCheckinAppointmentModel;

    /**
     * 
     * @return
     *     The paymentsConfirmModel
     */
    public PaymentsConfirmModel getPaymentsConfirmModel() {
        return paymentsConfirmModel;
    }

    /**
     * 
     * @param paymentsConfirmModel
     *     The confirm_payment
     */
    public void setPaymentsConfirmModel(PaymentsConfirmModel paymentsConfirmModel) {
        this.paymentsConfirmModel = paymentsConfirmModel;
    }

    /**
     * 
     * @return
     *     The paymentsPostPayModel
     */
    public PaymentsPostPayModel getPaymentsPostPayModel() {
        return paymentsPostPayModel;
    }

    /**
     * 
     * @param paymentsPostPayModel
     *     The post_payment
     */
    public void setPaymentsPostPayModel(PaymentsPostPayModel paymentsPostPayModel) {
        this.paymentsPostPayModel = paymentsPostPayModel;
    }

    /**
     * 
     * @return
     *     The paymentsPostCreditCardsModel
     */
    public PaymentsPostCreditCardsModel getPaymentsPostCreditCardsModel() {
        return paymentsPostCreditCardsModel;
    }

    /**
     * 
     * @param paymentsPostCreditCardsModel
     *     The post_credit_cards
     */
    public void setPaymentsPostCreditCardsModel(PaymentsPostCreditCardsModel paymentsPostCreditCardsModel) {
        this.paymentsPostCreditCardsModel = paymentsPostCreditCardsModel;
    }

    /**
     * 
     * @return
     *     The paymentsDeleteCreditCardsModel
     */
    public PaymentsDeleteCreditCardsModel getPaymentsDeleteCreditCardsModel() {
        return paymentsDeleteCreditCardsModel;
    }

    /**
     * 
     * @param paymentsDeleteCreditCardsModel
     *     The delete_credit_cards
     */
    public void setPaymentsDeleteCreditCardsModel(PaymentsDeleteCreditCardsModel paymentsDeleteCreditCardsModel) {
        this.paymentsDeleteCreditCardsModel = paymentsDeleteCreditCardsModel;
    }

    /**
     * 
     * @return
     *     The paymentsCheckinAppointmentModel
     */
    public PaymentsCheckinAppointmentModel getPaymentsCheckinAppointmentModel() {
        return paymentsCheckinAppointmentModel;
    }

    /**
     * 
     * @param paymentsCheckinAppointmentModel
     *     The checkin_appointment
     */
    public void setPaymentsCheckinAppointmentModel(PaymentsCheckinAppointmentModel paymentsCheckinAppointmentModel) {
        this.paymentsCheckinAppointmentModel = paymentsCheckinAppointmentModel;
    }

}
