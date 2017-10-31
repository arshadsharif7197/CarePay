package com.carecloud.carepaylibray.payments.models.history;

import android.support.annotation.StringDef;

import com.carecloud.carepaylibray.payments.models.IntegratedPatientPaymentLineItem;
import com.google.gson.annotations.SerializedName;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by lmenendez on 9/28/17
 */

public class PaymentHistoryLineItem extends IntegratedPatientPaymentLineItem {
    public static final String STATUS_SUCCEEDED = "succeeded";
    public static final String STATUS_FAILED = "failed";

    @StringDef({STATUS_SUCCEEDED, STATUS_FAILED})
    @Retention(RetentionPolicy.SOURCE)
    public @interface LineItemStatus{}

    @SerializedName("processing_id")
    private String lineItemId;

    @SerializedName("papi_processing_exhausted")
    private boolean papiProcessingExhausted = false;

    @SerializedName("cc_persisted")
    private boolean persisted = false;

    @SerializedName("merchant_service_transaction_id")
    private String transactionId;

    @SerializedName("papi_charge_id")
    private String papiChargeId;

    @SerializedName("card_used_last_four")
    private String cardNumber;

    @SerializedName("cc_credit_transaction_id")
    private String creditTransactionId;

    @SerializedName("status")
    private @LineItemStatus String status;

    private transient boolean checked = true;

    public boolean isPapiProcessingExhausted() {
        return papiProcessingExhausted;
    }

    public void setPapiProcessingExhausted(boolean papiProcessingExhausted) {
        this.papiProcessingExhausted = papiProcessingExhausted;
    }

    public boolean isPersisted() {
        return persisted;
    }

    public void setPersisted(boolean persisted) {
        this.persisted = persisted;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getPapiChargeId() {
        return papiChargeId;
    }

    public void setPapiChargeId(String papiChargeId) {
        this.papiChargeId = papiChargeId;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCreditTransactionId() {
        return creditTransactionId;
    }

    public void setCreditTransactionId(String creditTransactionId) {
        this.creditTransactionId = creditTransactionId;
    }

    public @LineItemStatus String getStatus() {
        return status;
    }

    public void setStatus(@LineItemStatus String status) {
        this.status = status;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getLineItemId() {
        return lineItemId;
    }

    public void setLineItemId(String lineItemId) {
        this.lineItemId = lineItemId;
    }



}
