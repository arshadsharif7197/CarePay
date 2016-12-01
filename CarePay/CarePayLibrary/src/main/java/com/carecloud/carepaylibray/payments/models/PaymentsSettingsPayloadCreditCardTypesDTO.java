package com.carecloud.carepaylibray.payments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Rahul on 11/30/16.
 */

public class PaymentsSettingsPayloadCreditCardTypesDTO {

    @SerializedName("master_card")
    @Expose
    private Boolean masterCard;
    @SerializedName("visa")
    @Expose
    private Boolean visa;
    @SerializedName("american_express")
    @Expose
    private Boolean americanExpress;
    @SerializedName("discover")
    @Expose
    private Boolean discover;

    /**
     *
     * @return
     * The masterCard
     */
    public Boolean getMasterCard() {
        return masterCard;
    }

    /**
     *
     * @param masterCard
     * The master_card
     */
    public void setMasterCard(Boolean masterCard) {
        this.masterCard = masterCard;
    }

    /**
     *
     * @return
     * The visa
     */
    public Boolean getVisa() {
        return visa;
    }

    /**
     *
     * @param visa
     * The visa
     */
    public void setVisa(Boolean visa) {
        this.visa = visa;
    }

    /**
     *
     * @return
     * The americanExpress
     */
    public Boolean getAmericanExpress() {
        return americanExpress;
    }

    /**
     *
     * @param americanExpress
     * The american_express
     */
    public void setAmericanExpress(Boolean americanExpress) {
        this.americanExpress = americanExpress;
    }

    /**
     *
     * @return
     * The discover
     */
    public Boolean getDiscover() {
        return discover;
    }

    /**
     *
     * @param discover
     * The discover
     */
    public void setDiscover(Boolean discover) {
        this.discover = discover;
    }
}
