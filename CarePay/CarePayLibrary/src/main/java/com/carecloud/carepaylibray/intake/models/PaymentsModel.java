package com.carecloud.carepaylibray.intake.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class PaymentsModel {

    @SerializedName("metadata")
    @Expose
    private MetadataPaymentModel metadata = new MetadataPaymentModel();
    @SerializedName("payload")
    @Expose
    private ArrayList<PayloadPaymentModel> payload = new ArrayList<>();

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
