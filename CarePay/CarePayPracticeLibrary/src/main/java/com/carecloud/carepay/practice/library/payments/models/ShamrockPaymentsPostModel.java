package com.carecloud.carepay.practice.library.payments.models;

import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentLineItem;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentPostModel;
import com.carecloud.carepaylibray.payments.models.postmodel.PapiPaymentMethod;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lmenendez on 11/9/17
 */

public class ShamrockPaymentsPostModel {

    @SerializedName("amount")
    private double amount = -1;

    @SerializedName("execution")
    private @IntegratedPaymentPostModel.ExecutionType String execution;

    @SerializedName("external_transaction_response")
    private JsonObject transactionResponse;

    @SerializedName("line_items")
    private List<IntegratedPaymentLineItem> lineItems = new ArrayList<>();

    @SerializedName("payment_method")
    private PapiPaymentMethod papiPaymentMethod = new PapiPaymentMethod();

    @SerializedName("metadata")
    private ShamrockPaymentMetadata metadata = new ShamrockPaymentMetadata();

    @SerializedName("payment_profile_id")
    private String paymentProfileId;

    @SerializedName("organization_id")
    private String organizationId;

    @SerializedName("deepstream_record_id")
    private String deepstreamId;

    public ShamrockPaymentMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(ShamrockPaymentMetadata metadata) {
        this.metadata = metadata;
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

    public String getDeepstreamId() {
        return deepstreamId;
    }

    public void setDeepstreamId(String deepstreamId) {
        this.deepstreamId = deepstreamId;
    }

    /**
     * Fill base Fields
     * @param postModel post model
     * @return this
     */
    public ShamrockPaymentsPostModel setIntegratedPaymentPostModel(IntegratedPaymentPostModel postModel){
        setAmount(postModel.getAmount());
        setExecution(postModel.getExecution());
        setLineItems(postModel.getLineItems());
        setPapiPaymentMethod(postModel.getPapiPaymentMethod());
        setTransactionResponse(postModel.getTransactionResponse());
        setMetadata(new ShamrockPaymentMetadata().setIntegratedPaymentMetadata(postModel.getMetadata()));

        return this;
    }

}
