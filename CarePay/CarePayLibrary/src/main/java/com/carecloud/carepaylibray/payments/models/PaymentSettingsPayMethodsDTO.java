package com.carecloud.carepaylibray.payments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Rahul on 11/30/16.
 */

public class PaymentSettingsPayMethodsDTO {

    @SerializedName("cash")
    @Expose
    private Boolean cash;
    @SerializedName("credit_card")
    @Expose
    private Boolean creditCard;
    @SerializedName("check")
    @Expose
    private Boolean check;
    @SerializedName("gift_card")
    @Expose
    private Boolean giftCard;
    @SerializedName("paypal")
    @Expose
    private Boolean paypal;
    @SerializedName("android_pay")
    @Expose
    private Boolean androidPay;

    /**
     *
     * @return
     * The cash
     */
    public Boolean getCash() {
        return cash;
    }

    /**
     *
     * @param cash
     * The cash
     */
    public void setCash(Boolean cash) {
        this.cash = cash;
    }

    /**
     *
     * @return
     * The creditCard
     */
    public Boolean getCreditCard() {
        return creditCard;
    }

    /**
     *
     * @param creditCard
     * The credit_card
     */
    public void setCreditCard(Boolean creditCard) {
        this.creditCard = creditCard;
    }

    /**
     *
     * @return
     * The check
     */
    public Boolean getCheck() {
        return check;
    }

    /**
     *
     * @param check
     * The check
     */
    public void setCheck(Boolean check) {
        this.check = check;
    }

    /**
     *
     * @return
     * The giftCard
     */
    public Boolean getGiftCard() {
        return giftCard;
    }

    /**
     *
     * @param giftCard
     * The gift_card
     */
    public void setGiftCard(Boolean giftCard) {
        this.giftCard = giftCard;
    }

    /**
     *
     * @return
     * The paypal
     */
    public Boolean getPaypal() {
        return paypal;
    }

    /**
     *
     * @param paypal
     * The paypal
     */
    public void setPaypal(Boolean paypal) {
        this.paypal = paypal;
    }

    /**
     *
     * @return
     * The androidPay
     */
    public Boolean getAndroidPay() {
        return androidPay;
    }

    /**
     *
     * @param androidPay
     * The android_pay
     */
    public void setAndroidPay(Boolean androidPay) {
        this.androidPay = androidPay;
    }

}
