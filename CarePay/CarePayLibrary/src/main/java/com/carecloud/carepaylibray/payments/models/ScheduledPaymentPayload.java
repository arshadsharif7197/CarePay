package com.carecloud.carepaylibray.payments.models;

import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentPostModel;
import com.carecloud.carepaylibray.payments.models.postmodel.PapiPaymentMethod;
import com.google.gson.annotations.SerializedName;

public class ScheduledPaymentPayload {

    @SerializedName("amount")
    private double amount;

    @SerializedName("execution")
    @IntegratedPaymentPostModel.ExecutionType
    private String execution;

    @SerializedName("payment_method")
    private PapiPaymentMethod paymentMethod;

    @SerializedName("payment_date")
    private String paymentDate;

    @SerializedName("status")
    @PaymentPlanDetailsDTO.PlanStatus
    private String status;

    @SerializedName("is_enabled")
    private boolean enabled = false;

    @SerializedName("is_cancelled")
    private boolean cancelled = false;

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getExecution() {
        return execution;
    }

    public void setExecution(String execution) {
        this.execution = execution;
    }

    public PapiPaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PapiPaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
