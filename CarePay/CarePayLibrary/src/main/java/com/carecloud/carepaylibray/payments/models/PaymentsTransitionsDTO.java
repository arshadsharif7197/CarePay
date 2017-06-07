
package com.carecloud.carepaylibray.payments.models;

import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PaymentsTransitionsDTO {

    @SerializedName("make_payment")
    @Expose
    private TransitionDTO makePayment = new TransitionDTO();
    @SerializedName("add_credit_card")
    @Expose
    private TransitionDTO addCreditCard = new TransitionDTO();
    @SerializedName("delete_credit_card")
    @Expose
    private PaymentsDeleteCreditCardsDTO deleteCreditCard = new PaymentsDeleteCreditCardsDTO();
    @SerializedName("add_payment_plan")
    @Expose
    private TransitionDTO addPaymentPlan = new TransitionDTO();
    @SerializedName("delete_payment_plan")
    @Expose
    private PayementsDeletePaymentPlanDTO deletePaymentPlan = new PayementsDeletePaymentPlanDTO();
    @SerializedName("modify_payment_plan")
    @Expose
    private PaymentsModifyPaymentPlanDTO modifyPaymentPlan = new PaymentsModifyPaymentPlanDTO();
    @SerializedName("logout")
    @Expose
    private TransitionDTO logout = new TransitionDTO();
    @SerializedName("queue_payment")
    @Expose
    private TransitionDTO queuePayment = new TransitionDTO();
    @SerializedName("continue")
    @Expose
    private TransitionDTO continueTransition = new TransitionDTO();

    /**
     * @return The makePayment
     */
    public TransitionDTO getMakePayment() {
        return makePayment;
    }

    /**
     * @param makePayment The make_payment
     */
    public void setMakePayment(TransitionDTO makePayment) {
        this.makePayment = makePayment;
    }

    /**
     * @return The addCreditCard
     */
    public TransitionDTO getAddCreditCard() {
        return addCreditCard;
    }

    /**
     * @param addCreditCard The add_credit_card
     */
    public void setAddCreditCard(TransitionDTO addCreditCard) {
        this.addCreditCard = addCreditCard;
    }

    /**
     * @return The deleteCreditCard
     */
    public PaymentsDeleteCreditCardsDTO getDeleteCreditCard() {
        return deleteCreditCard;
    }

    /**
     * @param deleteCreditCard The delete_credit_card
     */
    public void setDeleteCreditCard(PaymentsDeleteCreditCardsDTO deleteCreditCard) {
        this.deleteCreditCard = deleteCreditCard;
    }

    /**
     * @return The addPaymentPlan
     */
    public TransitionDTO getAddPaymentPlan() {
        return addPaymentPlan;
    }

    /**
     * @param addPaymentPlan The add_payment_plan
     */
    public void setAddPaymentPlan(TransitionDTO addPaymentPlan) {
        this.addPaymentPlan = addPaymentPlan;
    }

    /**
     * @return The deletePaymentPlan
     */
    public PayementsDeletePaymentPlanDTO getDeletePaymentPlan() {
        return deletePaymentPlan;
    }

    /**
     * @param deletePaymentPlan The delete_payment_plan
     */
    public void setDeletePaymentPlan(PayementsDeletePaymentPlanDTO deletePaymentPlan) {
        this.deletePaymentPlan = deletePaymentPlan;
    }

    /**
     * @return The modifyPaymentPlan
     */
    public PaymentsModifyPaymentPlanDTO getModifyPaymentPlan() {
        return modifyPaymentPlan;
    }

    /**
     * @param modifyPaymentPlan The modify_payment_plan
     */
    public void setModifyPaymentPlan(PaymentsModifyPaymentPlanDTO modifyPaymentPlan) {
        this.modifyPaymentPlan = modifyPaymentPlan;
    }

    /**
     * @return the logout Transition
     */
    public TransitionDTO getLogout() {
        return logout;
    }

    /**
     * @param logout the logout transition
     */
    public void setLogout(TransitionDTO logout) {
        this.logout = logout;
    }

    public TransitionDTO getQueuePayment() {
        return queuePayment;
    }

    public void setQueuePayment(TransitionDTO queuePayment) {
        this.queuePayment = queuePayment;
    }

    public TransitionDTO getContinueTransition() {
        return continueTransition;
    }

    public void setContinueTransition(TransitionDTO continueTransition) {
        this.continueTransition = continueTransition;
    }
}
