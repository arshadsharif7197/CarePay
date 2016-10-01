package com.carecloud.carepaylibray.intake.models;

import java.util.ArrayList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PaymentsModel {

    @SerializedName("metadata")
    @Expose
    private MetadataPaymentModel metadata;
    @SerializedName("payload")
    @Expose
    private ArrayList<PayloadPaymentModel> payload = new ArrayList<PayloadPaymentModel>();

    /**
     * 
     * @return
     *     The metadata
     */
    public MetadataPaymentModel getMetadata() {
        return metadata;
    }

    /**
     * 
     * @param metadata
     *     The metadata
     */
    public void setMetadata(MetadataPaymentModel metadata) {
        this.metadata = metadata;
    }

    /**
     * 
     * @return
     *     The payload
     */
    public ArrayList<PayloadPaymentModel> getPayload() {
        return payload;
    }

    /**
     * 
     * @param payload
     *     The payload
     */
    public void setPayload(ArrayList<PayloadPaymentModel> payload) {
        this.payload = payload;
    }

}
