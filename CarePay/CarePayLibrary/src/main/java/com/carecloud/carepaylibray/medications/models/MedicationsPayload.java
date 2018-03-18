package com.carecloud.carepaylibray.medications.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lmenendez on 2/16/17.
 */

public class MedicationsPayload {

    @SerializedName("payload")
    @Expose
    private List<MedicationsObject> payload = new ArrayList<>();

    @SerializedName("metadata")
    @Expose
    private MedicationsPayloadMetadata metadata = new MedicationsPayloadMetadata();

    public MedicationsPayloadMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(MedicationsPayloadMetadata metadata) {
        this.metadata = metadata;
    }

    public List<MedicationsObject> getPayload() {
        return payload;
    }
}
