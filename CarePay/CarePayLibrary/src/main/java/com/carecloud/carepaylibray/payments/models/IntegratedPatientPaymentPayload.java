package com.carecloud.carepaylibray.payments.models;

import com.carecloud.carepaylibray.payments.models.postmodel.PapiPaymentMethod;
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

    @SerializedName("papi_errors")
    private List<ProcessingError> processingErrors = new ArrayList<>();

    @SerializedName("payment_group_id")
    private String paymentId;

    @SerializedName("payment_method")
    private PapiPaymentMethod paymentMethod;

    @SerializedName("metadata")
    private IntegratedPatientPaymentMetadata metadata;

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

    public List<ProcessingError> getProcessingErrors() {
        return processingErrors;
    }

    public void setProcessingErrors(List<ProcessingError> processingErrors) {
        this.processingErrors = processingErrors;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public PapiPaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PapiPaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public IntegratedPatientPaymentMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(IntegratedPatientPaymentMetadata metadata) {
        this.metadata = metadata;
    }

    /**
     * Calculate total of paid line items
     * @return total
     */
    public double getTotalPaid(){
        double total = 0D;
        for(IntegratedPatientPaymentLineItem lineItem : getLineItems()){
            if(lineItem.isProcessed()){
                total+=lineItem.getAmount();
            }
        }
        return total;
    }

    public class ProcessingError {

        @SerializedName("id")
        private String id;

        @SerializedName("error")
        private String error;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }
    }
}
