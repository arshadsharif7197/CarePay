package com.carecloud.carepaylibray.medications.models;

import com.carecloud.carepaylibray.interfaces.DTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 2/15/17.
 */

public class MedicationsAllergiesResultsModel implements DTO {

    @SerializedName("metadata")
    @Expose
    private MedicationAllergiesMetadata metadata = new MedicationAllergiesMetadata();

    @SerializedName("payload")
    @Expose
    private MedicationAllergiesPayload payload = new MedicationAllergiesPayload();

    @SerializedName("state")
    @Expose
    private String state;

    public MedicationAllergiesMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(MedicationAllergiesMetadata metadata) {
        this.metadata = metadata;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public MedicationAllergiesPayload getPayload() {
        return payload;
    }

    public void setPayload(MedicationAllergiesPayload payload) {
        this.payload = payload;
    }
}
