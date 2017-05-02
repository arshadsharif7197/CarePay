package com.carecloud.carepay.practice.library.signin.dtos;

import com.carecloud.carepaylibray.interfaces.DTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 3/9/17.
 */

public class PracticeSelectionDTO implements DTO{

    @SerializedName("metadata")
    @Expose
    private PracticeSelectionMetadata metadata = new PracticeSelectionMetadata();

    @SerializedName("payload")
    @Expose
    private PracticeSelectionPayloadDTO payload = new PracticeSelectionPayloadDTO();

    @SerializedName("state")
    String state;

    public PracticeSelectionMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(PracticeSelectionMetadata metadata) {
        this.metadata = metadata;
    }

    public PracticeSelectionPayloadDTO getPayload() {
        return payload;
    }

    public void setPayload(PracticeSelectionPayloadDTO payload) {
        this.payload = payload;
    }
}
