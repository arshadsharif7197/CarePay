package com.carecloud.carepaylibray.payments.models;

import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentPostModel;
import com.carecloud.carepaylibray.payments.models.postmodel.PapiPaymentMethod;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentPlanLineItem;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lmenendez on 1/24/18
 */

public class PaymentPlanPayloadDTO {

    @SerializedName("amount")
    @Expose
    private double amount;

    @SerializedName("execution")
    @Expose
    @IntegratedPaymentPostModel.ExecutionType
    private String execution;

    @SerializedName("payment_method")
    @Expose
    private PapiPaymentMethod paymentMethod = new PapiPaymentMethod();

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("line_items")
    @Expose
    private List<PaymentPlanLineItem> lineItems = new ArrayList<>();

    @SerializedName("payment_plan")
    @Expose
    private PaymentPlanDetailsDTO paymentPlanDetails = new PaymentPlanDetailsDTO();

    @SerializedName("amount_paid")
    @Expose
    private double amountPaid;

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @IntegratedPaymentPostModel.ExecutionType
    public String getExecution() {
        return execution;
    }

    public void setExecution(@IntegratedPaymentPostModel.ExecutionType String execution) {
        this.execution = execution;
    }

    public PapiPaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PapiPaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<PaymentPlanLineItem> getLineItems() {
        return lineItems;
    }

    public void setLineItems(List<PaymentPlanLineItem> lineItems) {
        this.lineItems = lineItems;
    }

    public PaymentPlanDetailsDTO getPaymentPlanDetails() {
        return paymentPlanDetails;
    }

    public void setPaymentPlanDetails(PaymentPlanDetailsDTO paymentPlanDetails) {
        this.paymentPlanDetails = paymentPlanDetails;
    }

    public double getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(double amountPaid) {
        this.amountPaid = amountPaid;
    }

    /**
     * calculate the percentage progress of the payment plan
     * @return percent progress 0-100
     */
    public int getPaymentPlanProgress(){
        return (int) (getAmountPaid()/getAmount() *100);
    }

}
