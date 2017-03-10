package com.carecloud.carepay.practice.library.signin.dtos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 3/9/17.
 */

public class PracticeSelectionResponseModel {

    @SerializedName("metadata")
    @Expose
    private PracticeSelectionMetadata metadata = new PracticeSelectionMetadata();

    @SerializedName("payload")
    @Expose
    private PracticeSelectionPayload payload = new PracticeSelectionPayload();

    @SerializedName("state")
    String state;

    public PracticeSelectionMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(PracticeSelectionMetadata metadata) {
        this.metadata = metadata;
    }

    public PracticeSelectionPayload getPayload() {
        return payload;
    }

    public void setPayload(PracticeSelectionPayload payload) {
        this.payload = payload;
    }
}
