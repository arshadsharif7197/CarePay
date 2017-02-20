package com.carecloud.carepay.practice.library.patientmode.dtos;


import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by prem_mourya on 11/15/2016.
 */

public class PatientModeSwitchPinResponseDTO {

    @SerializedName("metadata")
    @Expose
    private JsonObject metadata;

    @SerializedName("payload")
    @Expose
    private PatientModeSwitchPinPaylodDTO payload = new PatientModeSwitchPinPaylodDTO();

    public JsonObject getMetadata() {
        return metadata;
    }

    public void setMetadata(JsonObject metadata) {
        this.metadata = metadata;
    }

    public PatientModeSwitchPinPaylodDTO getPayload() {
        return payload;
    }

    public void setPayload(PatientModeSwitchPinPaylodDTO payload) {
        this.payload = payload;
    }
}
