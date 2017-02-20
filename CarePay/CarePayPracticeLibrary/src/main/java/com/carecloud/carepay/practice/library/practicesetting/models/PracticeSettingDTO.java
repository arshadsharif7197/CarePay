package com.carecloud.carepay.practice.library.practicesetting.models;

/**
 * Created by prem_mourya on 10/24/2016.
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PracticeSettingDTO implements Serializable {

    @SerializedName("metadata")
    @Expose
    private PracticeSettingMetadataDTO metadata = new PracticeSettingMetadataDTO();
    @SerializedName("payload")
    @Expose
    private PracticeSettingPayloadDTO payload = new PracticeSettingPayloadDTO();
    @SerializedName("state")
    @Expose
    private String state;

    /**
     *
     * @return
     *     The metadata
     */
    public PracticeSettingMetadataDTO getMetadata() {
        return metadata;
    }

    /**
     *
     * @param metadata
     *     The metadata
     */
    public void setMetadata(PracticeSettingMetadataDTO metadata) {
        this.metadata = metadata;
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

    public PracticeSettingPayloadDTO getPayload() {
        return payload;
    }

    public void setPayload(PracticeSettingPayloadDTO payload) {
        this.payload = payload;
    }
}

