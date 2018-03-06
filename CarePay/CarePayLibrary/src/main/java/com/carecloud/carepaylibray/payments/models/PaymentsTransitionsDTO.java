
package com.carecloud.carepaylibray.payments.models;

import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PaymentsTransitionsDTO {

    @SerializedName(value = "make_payment", alternate = "retail_payment")
    @Expose
    private TransitionDTO makePayment = new TransitionDTO();
    @SerializedName("add_credit_card")
    @Expose
    private TransitionDTO addCreditCard = new TransitionDTO();
    @SerializedName("delete_credit_card")
    @Expose
    private PaymentsDeleteCreditCardsDTO deleteCreditCard = new PaymentsDeleteCreditCardsDTO();
    @SerializedName("logout")
    @Expose
    private TransitionDTO logout = new TransitionDTO();
    @SerializedName("queue_payment")
    @Expose
    private TransitionDTO queuePayment = new TransitionDTO();
    @SerializedName("continue")
    @Expose
    private TransitionDTO continueTransition = new TransitionDTO();
    @SerializedName("practice_payments")
    @Expose
    private TransitionDTO practicePayments = new TransitionDTO();
    @SerializedName("refund_payment")
    @Expose
    private TransitionDTO refundPayment = new TransitionDTO();
    @SerializedName("record_payment")
    @Expose
    private TransitionDTO recordPayment = new TransitionDTO();
    @SerializedName("create_payment_plan")
    @Expose
    private TransitionDTO createPaymentPlan = new TransitionDTO();
    @SerializedName("delete_payment_plan")
    @Expose
    private TransitionDTO deletePaymentPlan = new TransitionDTO();
    @SerializedName("update_payment_plan")
    @Expose
    private TransitionDTO updatePaymentPlan = new TransitionDTO();
    @SerializedName("plan_payment")
    @Expose
    private TransitionDTO makePlanPayment = new TransitionDTO();
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

    public TransitionDTO getPracticePayments() {
        return practicePayments;
    }

    public void setPracticePayments(TransitionDTO practicePayments) {
        this.practicePayments = practicePayments;
    }

    public TransitionDTO getRefundPayment() {
        return refundPayment;
    }

    public void setRefundPayment(TransitionDTO refundPayment) {
        this.refundPayment = refundPayment;
    }

    public TransitionDTO getRecordPayment() {
        return recordPayment;
    }

    public void setRecordPayment(TransitionDTO recordPayment) {
        this.recordPayment = recordPayment;
    }

    public TransitionDTO getCreatePaymentPlan() {
        return createPaymentPlan;
    }

    public void setCreatePaymentPlan(TransitionDTO createPaymentPlan) {
        this.createPaymentPlan = createPaymentPlan;
    }

    public TransitionDTO getDeletePaymentPlan() {
        return deletePaymentPlan;
    }

    public void setDeletePaymentPlan(TransitionDTO deletePaymentPlan) {
        this.deletePaymentPlan = deletePaymentPlan;
    }

    public TransitionDTO getUpdatePaymentPlan() {
        return updatePaymentPlan;
    }

    public void setUpdatePaymentPlan(TransitionDTO updatePaymentPlan) {
        this.updatePaymentPlan = updatePaymentPlan;
    }

    public TransitionDTO getMakePlanPayment() {
        return makePlanPayment;
    }

    public void setMakePlanPayment(TransitionDTO makePlanPayment) {
        this.makePlanPayment = makePlanPayment;
    }
}
