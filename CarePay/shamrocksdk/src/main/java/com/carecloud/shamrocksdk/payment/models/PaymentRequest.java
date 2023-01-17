package com.carecloud.shamrocksdk.payment.models;

import com.carecloud.shamrocksdk.payment.models.defs.ExecDef;
import com.carecloud.shamrocksdk.payment.models.defs.StateDef;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Model Class for handling Payment Requests in DeepStream
 */

public class PaymentRequest {

    @SerializedName("state")
    @StateDef.PaymentRequestState
    private String state;

    @SerializedName("execution")
    @ExecDef.ExecutionType
    private String executionType;

    @SerializedName("organization_id")
    private String organizationId;

    @SerializedName("payment_profile_id")
    private String paymentProfileId;

    @SerializedName("amount")
    private double amount;

    @SerializedName("payment_method")
    private PaymentMethod paymentMethod;

    @SerializedName("provider_id")
    private String providerId;

    @SerializedName("location_id")
    private String locationId;

    @SerializedName("routeToken")
    private String routeToken;

    @SerializedName("line_items")
    private List<PaymentLineItem> paymentLineItems = new ArrayList<>();

    @SerializedName("external_transaction_response")
    private JsonObject transactionResponse;

    @SerializedName("metadata")
    private JsonObject metadata;

    @SerializedName("comments")
    private String comments;

    @SerializedName("deepstream_record_id")
    private String deepstreamId;

    @SerializedName("created_at")
    private String created;

    @SerializedName("updated_at")
    private String updated;

    @SerializedName("attempted_amount")
    private Double attemptedAmount;

    @SerializedName("reference_number")
    private String referenceNumber;

    @SerializedName("request_errors")
    private List<RequestError> requestErrors;

    //hidden properties
    @SerializedName("payment_captured")
    private boolean paymentCaptured;

    @SerializedName("papi_charges")
    private JsonElement papiCharges;

    @SerializedName("cc_credit_transactions")
    private JsonElement ccCreditTransactions;

    @SerializedName("papi_errors")
    private JsonElement papiErrors;

    @SerializedName("cc_errors")
    private JsonElement ccErrors;

    @SerializedName("queuing_errors")
    private JsonElement queueingErrors;

    @SerializedName("queued")
    private boolean queued;

    @SerializedName("retries_exhausted")
    private boolean retriesExhausted;

    @SerializedName("queuing_retries_exhausted")
    private boolean queuingRetriesExhausted;

    @SerializedName("payment_group_id")
    private String paymentGroupId;

    @SerializedName("most_recent_refund_request")
    private String mostRecentRefundRequest;

    @SerializedName("refund_requests")
    private JsonElement refundRequests;

    @SerializedName("processing_locked")
    private boolean processingLocked;

    public Double getAttemptedAmount() {
        return attemptedAmount;
    }

    public void setAttemptedAmount(Double attemptedAmount) {
        this.attemptedAmount = attemptedAmount;
    }

    public String getComments() {
        return comments;
    }

    public void setComment(String comments) {
        this.comments= comments;
    }

    public @StateDef.PaymentRequestState String getState() {
        return state;
    }

    public void setState(@StateDef.PaymentRequestState String state) {
        this.state = state;
    }

    public @ExecDef.ExecutionType String getExecutionType() {
        return executionType;
    }

    public void setExecutionType(@ExecDef.ExecutionType String executionType) {
        this.executionType = executionType;
    }

    public JsonObject getMetadata() {
        return metadata;
    }

    public void setMetadata(JsonObject metadata) {
        this.metadata = metadata;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getPaymentProfileId() {
        return paymentProfileId;
    }

    public void setPaymentProfileId(String paymentProfileId) {
        this.paymentProfileId = paymentProfileId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getRouteToken() {
        return routeToken;
    }

    public void setRouteToken(String routeToken) {
        this.routeToken = routeToken;
    }

    public List<PaymentLineItem> getPaymentLineItems() {
        return paymentLineItems;
    }

    public void setPaymentLineItems(List<PaymentLineItem> paymentLineItems) {
        this.paymentLineItems = paymentLineItems;
    }

    public JsonObject getTransactionResponse() {
        return transactionResponse;
    }

    public void setTransactionResponse(JsonObject transactionResponse) {
        this.transactionResponse = transactionResponse;
    }

    public String getDeepstreamId() {
        return deepstreamId;
    }

    public String getCreated() {
        return created;
    }

    public String getUpdated() {
        return updated;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public List<RequestError> getRequestErrors() {
        return requestErrors;
    }

    public void setRequestErrors(List<RequestError> requestErrors) {
        this.requestErrors = requestErrors;
    }
}
