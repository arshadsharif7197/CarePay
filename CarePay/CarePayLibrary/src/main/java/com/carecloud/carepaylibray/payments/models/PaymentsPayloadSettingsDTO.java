package com.carecloud.carepaylibray.payments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Rahul on 11/30/16.
 */

public class PaymentsPayloadSettingsDTO {

    @SerializedName("metadata")
    @Expose
    private PendingBalanceMetadataDTO metadata = new PendingBalanceMetadataDTO();
    @SerializedName("payload")
    @Expose
    private PaymentsSettingsPayloadDTO payload = new PaymentsSettingsPayloadDTO();
    @SerializedName("practice_mgmt")
    @Expose
    private String practiceMgmt;
    @SerializedName("practice_id")
    @Expose
    private String practiceID;

    /**
     *
     * @return
     * The metadata
     */
    public PendingBalanceMetadataDTO getMetadata() {
        return metadata;
    }

    /**
     *
     * @param metadata
     * The metadata
     */
    public void setMetadata(PendingBalanceMetadataDTO metadata) {
        this.metadata = metadata;
    }

    /**
     *
     * @return
     * The payload
     */
    public PaymentsSettingsPayloadDTO getPayload() {
        return payload;
    }

    /**
     *
     * @param payload
     * The payload
     */
    public void setPayload(PaymentsSettingsPayloadDTO payload) {
        this.payload = payload;
    }

    public String getPracticeMgmt() {
        return practiceMgmt;
    }

    public void setPracticeMgmt(String practiceMgmt) {
        this.practiceMgmt = practiceMgmt;
    }

    public String getPracticeID() {
        return practiceID;
    }

    public void setPracticeID(String practiceID) {
        this.practiceID = practiceID;
    }
}
