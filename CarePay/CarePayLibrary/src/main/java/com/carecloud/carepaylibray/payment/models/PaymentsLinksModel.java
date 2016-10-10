
package com.carecloud.carepaylibray.payment.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PaymentsLinksModel {

    @SerializedName("paymentsSelfRefreshModel")
    @Expose
    private PaymentsSelfRefreshModel paymentsSelfRefreshModel;
    @SerializedName("paymentsDemographicsModel")
    @Expose
    private PaymentsLinksModel paymentsDemographicsModel;
    @SerializedName("appointments")
    @Expose
    private PaymentsAppointmentsModel appointments;
    @SerializedName("patient_balances")
    @Expose
    private PaymentsPatientBalancesModel paymentsPatientBalancesModel;
    @SerializedName("payment_methods")
    @Expose
    private PaymentsMethodsModel paymentsMethodsModel;
    @SerializedName("credit_cards")
    @Expose
    private PaymentsCreditCardsModel paymentsCreditCardsModel;
    @SerializedName("payment_plans")
    @Expose
    private PaymentsPlansModel paymentsPlansModel;
    @SerializedName("payment_history")
    @Expose
    private PaymentsHistoryModel paymentsHistoryModel;

    /**
     * 
     * @return
     *     The paymentsSelfRefreshModel
     */
    public PaymentsSelfRefreshModel getPaymentsSelfRefreshModel() {
        return paymentsSelfRefreshModel;
    }

    /**
     * 
     * @param paymentsSelfRefreshModel
     *     The paymentsSelfRefreshModel
     */
    public void setPaymentsSelfRefreshModel(PaymentsSelfRefreshModel paymentsSelfRefreshModel) {
        this.paymentsSelfRefreshModel = paymentsSelfRefreshModel;
    }

    /**
     * 
     * @return
     *     The paymentsDemographicsModel
     */
    public PaymentsLinksModel getPaymentsDemographicsModel() {
        return paymentsDemographicsModel;
    }

    /**
     * 
     * @param paymentsDemographicsModel
     *     The paymentsDemographicsModel
     */
    public void setPaymentsDemographicsModel(PaymentsLinksModel paymentsDemographicsModel) {
        this.paymentsDemographicsModel = paymentsDemographicsModel;
    }

    /**
     * 
     * @return
     *     The appointments
     */
    public PaymentsAppointmentsModel getAppointments() {
        return appointments;
    }

    /**
     * 
     * @param appointments
     *     The appointments
     */
    public void setAppointments(PaymentsAppointmentsModel appointments) {
        this.appointments = appointments;
    }

    /**
     * 
     * @return
     *     The paymentsPatientBalancesModel
     */
    public PaymentsPatientBalancesModel getPaymentsPatientBalancesModel() {
        return paymentsPatientBalancesModel;
    }

    /**
     * 
     * @param paymentsPatientBalancesModel
     *     The patient_balances
     */
    public void setPaymentsPatientBalancesModel(PaymentsPatientBalancesModel paymentsPatientBalancesModel) {
        this.paymentsPatientBalancesModel = paymentsPatientBalancesModel;
    }

    /**
     * 
     * @return
     *     The paymentsMethodsModel
     */
    public PaymentsMethodsModel getPaymentsMethodsModel() {
        return paymentsMethodsModel;
    }

    /**
     * 
     * @param paymentsMethodsModel
     *     The payment_methods
     */
    public void setPaymentsMethodsModel(PaymentsMethodsModel paymentsMethodsModel) {
        this.paymentsMethodsModel = paymentsMethodsModel;
    }

    /**
     * 
     * @return
     *     The paymentsCreditCardsModel
     */
    public PaymentsCreditCardsModel getPaymentsCreditCardsModel() {
        return paymentsCreditCardsModel;
    }

    /**
     * 
     * @param paymentsCreditCardsModel
     *     The credit_cards
     */
    public void setPaymentsCreditCardsModel(PaymentsCreditCardsModel paymentsCreditCardsModel) {
        this.paymentsCreditCardsModel = paymentsCreditCardsModel;
    }

    /**
     * 
     * @return
     *     The paymentsPlansModel
     */
    public PaymentsPlansModel getPaymentsPlansModel() {
        return paymentsPlansModel;
    }

    /**
     * 
     * @param paymentsPlansModel
     *     The payment_plans
     */
    public void setPaymentsPlansModel(PaymentsPlansModel paymentsPlansModel) {
        this.paymentsPlansModel = paymentsPlansModel;
    }

    /**
     * 
     * @return
     *     The paymentsHistoryModel
     */
    public PaymentsHistoryModel getPaymentsHistoryModel() {
        return paymentsHistoryModel;
    }

    /**
     * 
     * @param paymentsHistoryModel
     *     The payment_history
     */
    public void setPaymentsHistoryModel(PaymentsHistoryModel paymentsHistoryModel) {
        this.paymentsHistoryModel = paymentsHistoryModel;
    }

}
