
package com.carecloud.carepaylibray.payment.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PaymentsLinksDTO {

    @SerializedName("self")
    @Expose
    private PaymentsSelfRefreshDTO paymentsSelfRefresh;
    @SerializedName("demographics")
    @Expose
    private PaymentsLinksDTO paymentsDemographics;
    @SerializedName("appointments")
    @Expose
    private PaymentsAppointmentsDTO appointments;
    @SerializedName("patient_balances")
    @Expose
    private PaymentsPatientBalancesDTO paymentsPatientBalances;
    @SerializedName("payment_methods")
    @Expose
    private PaymentsMethodsDTO paymentsMethods;
    @SerializedName("credit_cards")
    @Expose
    private PaymentsCreditCardsDTO paymentsCreditCards;
    @SerializedName("payment_plans")
    @Expose
    private PaymentsPlansDTO paymentsPlans;
    @SerializedName("payment_history")
    @Expose
    private PaymentsHistoryDTO paymentsHistory;

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
    public PaymentsLinksDTO getPaymentsDemographics() {
        return paymentsDemographics;
    }

    /**
     * 
     * @param paymentsDemographics
     *     The paymentsDemographics
     */
    public void setPaymentsDemographics(PaymentsLinksDTO paymentsDemographics) {
        this.paymentsDemographics = paymentsDemographics;
    }

    /**
     * 
     * @return
     *     The appointments
     */
    public PaymentsAppointmentsDTO getAppointments() {
        return appointments;
    }

    /**
     * 
     * @param appointments
     *     The appointments
     */
    public void setAppointments(PaymentsAppointmentsDTO appointments) {
        this.appointments = appointments;
    }

    /**
     * 
     * @return
     *     The paymentsPatientBalancesDTO
     */
    public PaymentsPatientBalancesDTO getPaymentsPatientBalances() {
        return paymentsPatientBalances;
    }

    /**
     * 
     * @param paymentsPatientBalances
     *     The patient_balances
     */
    public void setPaymentsPatientBalances(PaymentsPatientBalancesDTO paymentsPatientBalances) {
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
    public PaymentsCreditCardsDTO getPaymentsCreditCards() {
        return paymentsCreditCards;
    }

    /**
     * 
     * @param paymentsCreditCards
     *     The credit_cards
     */
    public void setPaymentsCreditCards(PaymentsCreditCardsDTO paymentsCreditCards) {
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
    public PaymentsHistoryDTO getPaymentsHistory() {
        return paymentsHistory;
    }

    /**
     * 
     * @param paymentsHistory
     *     The payment_history
     */
    public void setPaymentsHistory(PaymentsHistoryDTO paymentsHistory) {
        this.paymentsHistory = paymentsHistory;
    }

}
