package com.carecloud.shamrocksdk.payment.models;

import com.carecloud.shamrocksdk.payment.models.defs.ExecDef;
import com.carecloud.shamrocksdk.payment.models.defs.StateDef;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Model Class for handling Payment Requests in DeepStream
 */

public class RefundRequest {

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

    @SerializedName("total_refund_amount")
    private double refundAmount;

    @SerializedName("payment_request_id")
    private String paymentRequestId;

    @SerializedName("merchant_service_transaction_id")
    private String merchantServiceId;

    @SerializedName("external_transaction_response")
    private JsonObject transactionResponse;

    @SerializedName("metadata")
    private JsonObject metadata;

    @SerializedName("comments")
    private String comments;

    @SerializedName("created_at")
    private String created;

    @SerializedName("updated_at")
    private String updated;

    @SerializedName("reference_number")
    private String referenceNumber;

    @SerializedName("request_errors")
    private List<RequestError> requestErrors;

    //hidden properties
    @SerializedName("deepstream_record_id")
    private String deepStreamId;

    @SerializedName("papi_charge_id")
    private String papiChargeId;

    @SerializedName("credit_transactions")
    private JsonElement creditTransactions;

    @SerializedName("papi_processing_exhausted")
    private boolean papiProcessingExhausted;

    @SerializedName("papi_processed")
    private boolean papiProcessed;

    @SerializedName("refund_captured")
    private boolean refundCaptured;

    @SerializedName("cc_refund_credit_transactions")
    private JsonElement ccRefundCreditTransactions;

    @SerializedName("papi_errors")
    private JsonElement papiErrors;

    @SerializedName("cc_errors")
    private JsonElement ccErrors;

    @SerializedName("queuing_errors")
    private JsonElement queuingErrors;

    @SerializedName("queued")
    private boolean queued;

    @SerializedName("retries_exhausted")
    private boolean retriesExhausted;

    @SerializedName("queuing_retries_exhausted")
    private boolean queuingRetriesExhausted;

    @SerializedName("papi_refund")
    private JsonElement papiRefund;

    @SerializedName("processing_locked")
    private boolean processingLocked;

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

    public double getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(double refundAmount) {
        this.refundAmount = refundAmount;
    }

    public JsonObject getTransactionResponse() {
        return transactionResponse;
    }

    public void setTransactionResponse(JsonObject transactionResponse) {
        this.transactionResponse = transactionResponse;
    }

    public JsonObject getMetadata() {
        return metadata;
    }

    public void setMetadata(JsonObject metadata) {
        this.metadata = metadata;
    }


    public String getPaymentRequestId() {
        return paymentRequestId;
    }

    public void setPaymentRequestId(String paymentRequestId) {
        this.paymentRequestId = paymentRequestId;
    }

    public String getMerchantServiceId() {
        return merchantServiceId;
    }

    public void setMerchantServiceId(String merchantServiceId) {
        this.merchantServiceId = merchantServiceId;
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

