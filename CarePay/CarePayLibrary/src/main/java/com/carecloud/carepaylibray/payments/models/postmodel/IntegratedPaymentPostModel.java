package com.carecloud.carepaylibray.payments.models.postmodel;

import android.support.annotation.StringDef;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lmenendez on 8/29/17
 */

public class IntegratedPaymentPostModel {
    public static final String EXECUTION_CLOVER = "clover";
    public static final String EXECUTION_ANDROID = "android_pay";
    public static final String EXECUTION_PAYEEZY = "payeezy";

    @StringDef({EXECUTION_CLOVER, EXECUTION_ANDROID, EXECUTION_PAYEEZY})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ExecutionType{};

    @SerializedName("amount")
    private double amount = -1;

    @SerializedName("execution")
    private @ExecutionType String execution;

    @SerializedName("external_transaction_response")
    private JsonObject transactionResponse;

    @SerializedName("metadata")
    private IntegratedPaymentMetadata metadata = new IntegratedPaymentMetadata();

    @SerializedName("line_items")
    private List<IntegratedPaymentLineItem> lineItems = new ArrayList<>();

    @SerializedName("payment_method")
    private PapiPaymentMethod papiPaymentMethod = new PapiPaymentMethod();

    @SerializedName("card_data")
    private IntegratedPaymentCardData cardData;

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public @ExecutionType String getExecution() {
        return execution;
    }

    public void setExecution(@ExecutionType String execution) {
        this.execution = execution;
    }

    public JsonObject getTransactionResponse() {
        return transactionResponse;
    }

    public void setTransactionResponse(JsonObject transactionResponse) {
        this.transactionResponse = transactionResponse;
    }

    public IntegratedPaymentMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(IntegratedPaymentMetadata metadata) {
        this.metadata = metadata;
    }

    public List<IntegratedPaymentLineItem> getLineItems() {
        return lineItems;
    }

    public void setLineItems(List<IntegratedPaymentLineItem> lineItems) {
        this.lineItems = lineItems;
    }

    public PapiPaymentMethod getPapiPaymentMethod() {
        return papiPaymentMethod;
    }

    public void setPapiPaymentMethod(PapiPaymentMethod papiPaymentMethod) {
        this.papiPaymentMethod = papiPaymentMethod;
    }

    public IntegratedPaymentCardData getCardData() {
        return cardData;
    }

    public void setCardData(IntegratedPaymentCardData cardData) {
        this.cardData = cardData;
    }

    public boolean isPaymentModelValid(){
        //// TODO: 8/29/17
        return true;
    }

    public void addLineItem(IntegratedPaymentLineItem paymentLineItem){
        lineItems.add(paymentLineItem);
    }

}
