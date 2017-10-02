
package com.carecloud.carepaylibray.payments.models;

import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PaymentsLinksDTO {

    @SerializedName("self")
    @Expose
    private PaymentsSelfRefreshDTO paymentsSelfRefresh = new PaymentsSelfRefreshDTO();
    @SerializedName("demographics")
    @Expose
    private TransitionDTO paymentsDemographics = new TransitionDTO();
    @SerializedName("appointments")
    @Expose
    private TransitionDTO appointments = new TransitionDTO();
    @SerializedName("patient_balances")
    @Expose
    private TransitionDTO paymentsPatientBalances = new TransitionDTO();
    @SerializedName("payment_methods")
    @Expose
    private PaymentsMethodsDTO paymentsMethods = new PaymentsMethodsDTO();
    @SerializedName("credit_cards")
    @Expose
    private TransitionDTO paymentsCreditCards = new TransitionDTO();
    @SerializedName("payment_plans")
    @Expose
    private PaymentsPlansDTO paymentsPlans = new PaymentsPlansDTO();
    @SerializedName("payment_history")
    @Expose
    private TransitionDTO paymentsHistory = new TransitionDTO();
    @SerializedName("find_patient")
    @Expose
    private TransitionDTO findPatient = new TransitionDTO();
    @SerializedName("payment_summary")
    @Expose
    private TransitionDTO paymentTransactionHistory = new TransitionDTO();

    /**
     * 
     * @return
     *     The paymentsSelfRefresh
     */
    public PaymentsSelfRefreshDTO getPaymentsSelfRefresh() {
        return paymentsSelfRefresh;
    }

    /**
     * 
     * @param paymentsSelfRefresh
     *     The paymentsSelfRefresh
     */
    public void setPaymentsSelfRefresh(PaymentsSelfRefreshDTO paymentsSelfRefresh) {
        this.paymentsSelfRefresh = paymentsSelfRefresh;
    }

    /**
     * 
     * @return
     *     The paymentsDemographics
     */
    public TransitionDTO getPaymentsDemographics() {
        return paymentsDemographics;
    }

    /**
     * 
     * @param paymentsDemographics
     *     The paymentsDemographics
     */
    public void setPaymentsDemographics(TransitionDTO paymentsDemographics) {
        this.paymentsDemographics = paymentsDemographics;
    }

    /**
     * 
     * @return
     *     The appointments
     */
    public TransitionDTO getAppointments() {
        return appointments;
    }

    /**
     * 
     * @param appointments
     *     The appointments
     */
    public void setAppointments(TransitionDTO appointments) {
        this.appointments = appointments;
    }

    /**
     * 
     * @return
     *     The paymentsPatientBalancesDTO
     */
    public TransitionDTO getPaymentsPatientBalances() {
        return paymentsPatientBalances;
    }

    /**
     * 
     * @param paymentsPatientBalances
     *     The patient_balances
     */
    public void setPaymentsPatientBalances(TransitionDTO paymentsPatientBalances) {
        this.paymentsPatientBalances = paymentsPatientBalances;
    }

    /**
     * 
     * @return
     *     The paymentsMethodsDTO
     */
    public PaymentsMethodsDTO getPaymentsMethods() {
        return paymentsMethods;
    }

    /**
     * 
     * @param paymentsMethods
     *     The payment_methods
     */
    public void setPaymentsMethods(PaymentsMethodsDTO paymentsMethods) {
        this.paymentsMethods = paymentsMethods;
    }

    /**
     * 
     * @return
     *     The paymentsCreditCardsDTO
     */
    public TransitionDTO getPaymentsCreditCards() {
        return paymentsCreditCards;
    }

    /**
     * 
     * @param paymentsCreditCards
     *     The credit_cards
     */
    public void setPaymentsCreditCards(TransitionDTO paymentsCreditCards) {
        this.paymentsCreditCards = paymentsCreditCards;
    }

    /**
     * 
     * @return
     *     The paymentsPlans
     */
    public PaymentsPlansDTO getPaymentsPlans() {
        return paymentsPlans;
    }

    /**
     * 
     * @param paymentsPlans
     *     The payment_plans
     */
    public void setPaymentsPlans(PaymentsPlansDTO paymentsPlans) {
        this.paymentsPlans = paymentsPlans;
    }

    /**
     * 
     * @return
     *     The paymentsHistory
     */
    public TransitionDTO getPaymentsHistory() {
        return paymentsHistory;
    }

    /**
     * 
     * @param paymentsHistory
     *     The payment_history
     */
    public void setPaymentsHistory(TransitionDTO paymentsHistory) {
        this.paymentsHistory = paymentsHistory;
    }

    public TransitionDTO getFindPatient() {
        return findPatient;
    }

    public void setFindPatient(TransitionDTO findPatient) {
        this.findPatient = findPatient;
    }

    public TransitionDTO getPaymentTransactionHistory() {
        return paymentTransactionHistory;
    }

    public void setPaymentTransactionHistory(TransitionDTO paymentTransactionHistory) {
        this.paymentTransactionHistory = paymentTransactionHistory;
    }
}
