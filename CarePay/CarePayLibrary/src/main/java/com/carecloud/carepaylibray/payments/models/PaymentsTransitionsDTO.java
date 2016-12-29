
package com.carecloud.carepaylibray.payments.models;

import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PaymentsTransitionsDTO {

    @SerializedName("make_payment")
    @Expose
    private TransitionDTO makePayment;
    @SerializedName("add_credit_card")
    @Expose
    private TransitionDTO addCreditCard;
    @SerializedName("delete_credit_card")
    @Expose
    private PaymentsDeleteCreditCardsDTO deleteCreditCard;
    @SerializedName("add_payment_plan")
    @Expose
    private TransitionDTO addPaymentPlan;
    @SerializedName("delete_payment_plan")
    @Expose
    private PayementsDeletePaymentPlanDTO deletePaymentPlan;
    @SerializedName("modify_payment_plan")
    @Expose
    private PaymentsModifyPaymentPlanDTO modifyPaymentPlan;

    /**
     *
     * @return
     * The makePayment
     */
    public TransitionDTO getMakePayment() {
        return makePayment;
    }

    /**
     *
     * @param makePayment
     * The make_payment
     */
    public void setMakePayment(TransitionDTO makePayment) {
        this.makePayment = makePayment;
    }

    /**
     *
     * @return
     * The addCreditCard
     */
    public TransitionDTO getAddCreditCard() {
        return addCreditCard;
    }

    /**
     *
     * @param addCreditCard
     * The add_credit_card
     */
    public void setAddCreditCard(TransitionDTO addCreditCard) {
        this.addCreditCard = addCreditCard;
    }

    /**
     *
     * @return
     * The deleteCreditCard
     */
    public PaymentsDeleteCreditCardsDTO getDeleteCreditCard() {
        return deleteCreditCard;
    }

    /**
     *
     * @param deleteCreditCard
     * The delete_credit_card
     */
    public void setDeleteCreditCard(PaymentsDeleteCreditCardsDTO deleteCreditCard) {
        this.deleteCreditCard = deleteCreditCard;
    }

    /**
     *
     * @return
     * The addPaymentPlan
     */
    public TransitionDTO getAddPaymentPlan() {
        return addPaymentPlan;
    }

    /**
     *
     * @param addPaymentPlan
     * The add_payment_plan
     */
    public void setAddPaymentPlan(TransitionDTO addPaymentPlan) {
        this.addPaymentPlan = addPaymentPlan;
    }

    /**
     *
     * @return
     * The deletePaymentPlan
     */
    public PayementsDeletePaymentPlanDTO getDeletePaymentPlan() {
        return deletePaymentPlan;
    }

    /**
     *
     * @param deletePaymentPlan
     * The delete_payment_plan
     */
    public void setDeletePaymentPlan(PayementsDeletePaymentPlanDTO deletePaymentPlan) {
        this.deletePaymentPlan = deletePaymentPlan;
    }

    /**
     *
     * @return
     * The modifyPaymentPlan
     */
    public PaymentsModifyPaymentPlanDTO getModifyPaymentPlan() {
        return modifyPaymentPlan;
    }

    /**
     *
     * @param modifyPaymentPlan
     * The modify_payment_plan
     */
    public void setModifyPaymentPlan(PaymentsModifyPaymentPlanDTO modifyPaymentPlan) {
        this.modifyPaymentPlan = modifyPaymentPlan;
    }

}
