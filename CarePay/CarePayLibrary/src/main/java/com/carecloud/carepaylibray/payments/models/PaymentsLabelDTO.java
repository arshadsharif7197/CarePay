
package com.carecloud.carepaylibray.payments.models;


import com.carecloud.carepaylibray.utils.StringUtil;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PaymentsLabelDTO {
    @SerializedName("payment_partial_amount_title")
    @Expose
    private String paymentPartialAmountTitle;
    @SerializedName("payment_pay_total_amount_button")
    @Expose
    private String paymentTotalAmountButton;
    @SerializedName("payment_partial_amount_button")
    @Expose
    private String paymentPartialAmountButton;

    /**
     *
     * @return paymentPartialAmountTitle
     */
    public String getPaymentPartialAmountTitle() {
        return StringUtil.isNullOrEmpty(paymentPartialAmountTitle) ? "undefined label" : paymentPartialAmountTitle;
    }

    /**
     *
     * @param paymentPartialAmountTitle
     */
    public void setPaymentPartialAmountTitle(String paymentPartialAmountTitle) {
        this.paymentPartialAmountTitle = paymentPartialAmountTitle;
    }

    /**
     *
     * @return paymentPartialAmountButton
     */
    public String getPaymentPartialAmountButton() {
        return StringUtil.isNullOrEmpty(paymentPartialAmountButton) ? "undefined label" : paymentPartialAmountButton;
    }

    /**
     *
     * @param paymentPartialAmountButton
     */
    public void setPaymentPartialAmountButton(String paymentPartialAmountButton) {
        this.paymentPartialAmountButton = paymentPartialAmountButton;
    }

    /**
     *
     * @return paymentTotalAmountTitle
     */
    public String getPaymentTotalAmountButton() {
        return StringUtil.isNullOrEmpty(paymentTotalAmountButton) ? "undefined label" : paymentTotalAmountButton;
    }

    /**
     *
     * @param paymentTotalAmountButton
     */
    public void setPaymentTotalAmountButton(String paymentTotalAmountButton) {
        this.paymentTotalAmountButton = paymentTotalAmountButton;
    }
}
