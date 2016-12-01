package com.carecloud.carepaylibray.payments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PaymentPayloadModel {

    @SerializedName("patient_credit_cards")
    @Expose
    private PaymentPatientCreditCardsDTO patientCreditCards;

    /**
     * @return The patientCreditCards
     */
    public PaymentPatientCreditCardsDTO getPatientCreditCards() {
        return patientCreditCards;
    }

    /**
     * @param patientCreditCards The patient_credit_cards
     */
    public void setPatientCreditCards(PaymentPatientCreditCardsDTO patientCreditCards) {
        this.patientCreditCards = patientCreditCards;
    }
}
