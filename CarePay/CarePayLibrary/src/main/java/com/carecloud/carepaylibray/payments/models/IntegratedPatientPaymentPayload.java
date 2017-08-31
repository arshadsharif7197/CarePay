package com.carecloud.carepaylibray.payments.models;

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lmenendez on 8/31/17
 */

public class IntegratedPatientPaymentPayload {

    @SerializedName("amount")
    private double amount;

    @SerializedName("line_items")
    private List<IntegratedPatientPaymentLineItem> lineItems = new ArrayList<>();

    @SerializedName("papi_processing_errors")
    private List<JsonElement> processingErrors = new ArrayList<>();

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public List<IntegratedPatientPaymentLineItem> getLineItems() {
        return lineItems;
    }

    public void setLineItems(List<IntegratedPatientPaymentLineItem> lineItems) {
        this.lineItems = lineItems;
    }

    public List<JsonElement> getProcessingErrors() {
        return processingErrors;
    }

    public void setProcessingErrors(List<JsonElement> processingErrors) {
        this.processingErrors = processingErrors;
    }
}
