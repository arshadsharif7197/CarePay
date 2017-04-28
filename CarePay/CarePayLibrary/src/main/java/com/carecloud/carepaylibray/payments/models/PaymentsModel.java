
package com.carecloud.carepaylibray.payments.models;

import com.carecloud.carepaylibray.interfaces.DTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PaymentsModel implements Serializable, DTO {

    @SerializedName("metadata")
    @Expose
    private PaymentsMetadataModel paymentsMetadata = new PaymentsMetadataModel();
    @SerializedName("payload")
    @Expose
    private PaymentsPayloadDTO paymentPayload = new PaymentsPayloadDTO();
    @SerializedName("state")
    @Expose
    private String state;

    /**
     * @return The paymentsMetadataDTO
     */
    public PaymentsMetadataModel getPaymentsMetadata() {
        return paymentsMetadata;
    }

    /**
     * @param paymentsMetadata The paymentsMetadata
     */
    public void setPaymentsMetadata(PaymentsMetadataModel paymentsMetadata) {
        this.paymentsMetadata = paymentsMetadata;
    }

    public PaymentsPayloadDTO getPaymentPayload() {
        return paymentPayload;
    }

    public void setPaymentPayload(PaymentsPayloadDTO paymentPayload) {
        this.paymentPayload = paymentPayload;
    }

    /**
     * @return The state
     */
    public String getState() {
        return state;
    }

    /**
     * @param state The state
     */
    public void setState(String state) {
        this.state = state;
    }

}
