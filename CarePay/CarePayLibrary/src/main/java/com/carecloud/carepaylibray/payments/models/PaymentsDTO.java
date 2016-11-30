
package com.carecloud.carepaylibray.payments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PaymentsDTO {

    @SerializedName("metadata")
    @Expose
    private PaymentsMetadataDTO paymentsMetadata;
    @SerializedName("state")
    @Expose
    private String state;

    /**
     * 
     * @return
     *     The paymentsMetadataDTO
     */
    public PaymentsMetadataDTO getPaymentsMetadata() {
        return paymentsMetadata;
    }

    /**
     * 
     * @param paymentsMetadata
     *     The paymentsMetadata
     */
    public void setPaymentsMetadata(PaymentsMetadataDTO paymentsMetadata) {
        this.paymentsMetadata = paymentsMetadata;
    }

    /**
     * 
     * @return
     *     The state
     */
    public String getState() {
        return state;
    }

    /**
     * 
     * @param state
     *     The state
     */
    public void setState(String state) {
        this.state = state;
    }

}
