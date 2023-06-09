package com.carecloud.carepaylibray.payments.models.history;

import androidx.annotation.StringDef;

import com.carecloud.carepaylibray.payments.models.IntegratedPatientPaymentLineItem;
import com.carecloud.carepaylibray.payments.models.IntegratedPatientPaymentPayload;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentPostModel;
import com.carecloud.carepaylibray.payments.models.postmodel.PapiPaymentMethod;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lmenendez on 9/28/17
 */

public class PaymentHistoryItemPayload {
    public static final String STATE_CREATED = "Initialized";
    public static final String STATE_ACKNOWLEDGED = "Acknowledged";
    public static final String STATE_WAITING = "Waiting";
    public static final String STATE_CAPTURED = "Captured";
    public static final String STATE_COMPLETED = "Completed";
    public static final String STATE_CANCELED = "Cancelled";
    public static final String STATE_PROCESSING = "Processing";
    public static final String STATE_ERRORED = "Errored";
    public static final String STATE_RECORDING  = "Recording";

    @StringDef({STATE_CREATED, STATE_ACKNOWLEDGED, STATE_CAPTURED, STATE_COMPLETED, STATE_CANCELED, STATE_PROCESSING, STATE_RECORDING, STATE_ERRORED, STATE_WAITING})
    @Retention(RetentionPolicy.SOURCE)
    @interface PaymentRequestState{}

    @SerializedName("amount")
    private double amount = -1;

    @SerializedName("execution")
    private @IntegratedPaymentPostModel.ExecutionType String execution;

    @SerializedName("external_transaction_response")
    private JsonObject transactionResponse;

    @SerializedName("metadata")
    private PaymentHistoryItemPayloadMetadata metadata = new PaymentHistoryItemPayloadMetadata();

    @SerializedName("line_items")
    private List<PaymentHistoryLineItem> lineItems = new ArrayList<>();

    @SerializedName("payment_method")
    private PapiPaymentMethod papiPaymentMethod = new PapiPaymentMethod();

    @SerializedName("deepstream_record_id")
    private String deepstreamRecordId;

    @SerializedName("payment_profile_id")
    private String paymentProfileId;

    @SerializedName("organization_id")
    private String organizationId;

    @SerializedName("state")
    private @PaymentRequestState String state;

    @SerializedName("papi_errors")
    private List<IntegratedPatientPaymentPayload.ProcessingError> processingErrors = new ArrayList<>();

    @SerializedName("payment_group_id")
    private String paymentGroupId;

    @SerializedName("confirmation")
    private String confirmation;

    @SerializedName("date")
    private String date;

    @SerializedName("refund_requests")
    private List<String> refundRequests = new ArrayList<>();

    @SerializedName("most_recent_refund_request")
    private String lastRefundRequest;

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

    public JsonObject getTransactionResponse() {
        return transactionResponse;
    }

    public void setTransactionResponse(JsonObject transactionResponse) {
        this.transactionResponse = transactionResponse;
    }

    public PaymentHistoryItemPayloadMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(PaymentHistoryItemPayloadMetadata metadata) {
        this.metadata = metadata;
    }

    public List<PaymentHistoryLineItem> getLineItems() {
        return lineItems;
    }

    public void setLineItems(List<PaymentHistoryLineItem> lineItems) {
        this.lineItems = lineItems;
    }

    public PapiPaymentMethod getPapiPaymentMethod() {
        return papiPaymentMethod;
    }

    public void setPapiPaymentMethod(PapiPaymentMethod papiPaymentMethod) {
        this.papiPaymentMethod = papiPaymentMethod;
    }

    public String getDeepstreamRecordId() {
        return deepstreamRecordId;
    }

    public void setDeepstreamRecordId(String deepstreamRecordId) {
        this.deepstreamRecordId = deepstreamRecordId;
    }

    public String getPaymentProfileId() {
        return paymentProfileId;
    }

    public void setPaymentProfileId(String paymentProfileId) {
        this.paymentProfileId = paymentProfileId;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public @PaymentRequestState String getState() {
        return state;
    }

    public void setState(@PaymentRequestState String state) {
        this.state = state;
    }

    public String getPaymentGroupId() {
        return paymentGroupId;
    }

    public void setPaymentGroupId(String paymentGroupId) {
        this.paymentGroupId = paymentGroupId;
    }

    public String getConfirmation() {
        return confirmation;
    }

    public void setConfirmation(String confirmation) {
        this.confirmation = confirmation;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<IntegratedPatientPaymentPayload.ProcessingError> getProcessingErrors() {
        return processingErrors;
    }

    public void setProcessingErrors(List<IntegratedPatientPaymentPayload.ProcessingError> processingErrors) {
        this.processingErrors = processingErrors;
    }

    public List<String> getRefundRequests() {
        return refundRequests;
    }

    public void setRefundRequests(List<String> refundRequests) {
        this.refundRequests = refundRequests;
    }

    public String getLastRefundRequest() {
        return lastRefundRequest;
    }

    public void setLastRefundRequest(String lastRefundRequest) {
        this.lastRefundRequest = lastRefundRequest;
    }



    /**
     * Calculate total of paid line items
     * @return total
     */
    public double getTotalPaid(){
        double total = 0D;

        if(getMetadata().isExternallyProcessed()){
            return amount;
        }

        for(IntegratedPatientPaymentLineItem lineItem : getLineItems()){
            if(lineItem.isProcessed()){
                total+=lineItem.getAmount();
            }
        }
        return total;
    }

    /**
     * Calculate total refunded amount
     * @return refund total
     */
    public double getTotalRefunded(){
        double total = 0D;

        for(PaymentHistoryLineItem lineItem : getLineItems()){
            total+=lineItem.getRefundedAmount();
        }
        return total;
    }

}
