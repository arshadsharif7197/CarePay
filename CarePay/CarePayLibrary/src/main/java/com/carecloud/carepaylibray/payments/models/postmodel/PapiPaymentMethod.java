package com.carecloud.carepaylibray.payments.models.postmodel;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 3/18/17
 */

public class PapiPaymentMethod {

    @SerializedName("type")
    private PapiPaymentMethodType papiPaymentMethodType;

    @SerializedName("id")
    private String papiPaymentID;

    @SerializedName("card_data")
    private IntegratedPaymentCardData cardData;

    public PapiPaymentMethodType getPapiPaymentMethodType() {
        return papiPaymentMethodType;
    }

    public void setPapiPaymentMethodType(PapiPaymentMethodType papiPaymentMethodType) {
        this.papiPaymentMethodType = papiPaymentMethodType;
    }

    public String getPapiPaymentID() {
        return papiPaymentID;
    }

    public void setPapiPaymentID(String papiPaymentID) {
        this.papiPaymentID = papiPaymentID;
    }

    /**
     * Validate the papi payment method
     * @return true if valid
     */
    public boolean isValid(){
        return papiPaymentMethodType != null &&
                getPapiPaymentID() != null;
    }

    public IntegratedPaymentCardData getCardData() {
        return cardData;
    }

    public void setCardData(IntegratedPaymentCardData cardData) {
        this.cardData = cardData;
    }
}
