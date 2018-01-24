package com.carecloud.carepaylibray.payments.models.postmodel;

import com.carecloud.carepaylibray.payments.models.PendingBalanceMetadataDTO;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lmenendez on 1/23/18
 */

public class PaymentPlanPostModel {

    @SerializedName("amount")
    private double amount;

    @SerializedName("description")
    private String description;

    @SerializedName("execution")
    @IntegratedPaymentPostModel.ExecutionType
    private String execution;

    @SerializedName("line_items")
    private List<PaymentPlanLineItem> lineItems = new ArrayList<>();

    @SerializedName("payment_method")
    private PapiPaymentMethod papiPaymentMethod = new PapiPaymentMethod();

    @SerializedName("payment_plan")
    private PaymentPlanModel paymentPlanModel = new PaymentPlanModel();

    @SerializedName("metadata")
    private PendingBalanceMetadataDTO metadata = new PendingBalanceMetadataDTO();

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @IntegratedPaymentPostModel.ExecutionType
    public String getExecution() {
        return execution;
    }

    public void setExecution(@IntegratedPaymentPostModel.ExecutionType String execution) {
        this.execution = execution;
    }

    public List<PaymentPlanLineItem> getLineItems() {
        return lineItems;
    }

    public void setLineItems(List<PaymentPlanLineItem> lineItems) {
        this.lineItems = lineItems;
    }

    public PapiPaymentMethod getPapiPaymentMethod() {
        return papiPaymentMethod;
    }

    public void setPapiPaymentMethod(PapiPaymentMethod papiPaymentMethod) {
        this.papiPaymentMethod = papiPaymentMethod;
    }

    public PaymentPlanModel getPaymentPlanModel() {
        return paymentPlanModel;
    }

    public void setPaymentPlanModel(PaymentPlanModel paymentPlanModel) {
        this.paymentPlanModel = paymentPlanModel;
    }

    public PendingBalanceMetadataDTO getMetadata() {
        return metadata;
    }

    public void setMetadata(PendingBalanceMetadataDTO metadata) {
        this.metadata = metadata;
    }
}
