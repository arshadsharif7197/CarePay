package com.carecloud.carepay.practice.library.patientmodecheckin.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 4/25/17.
 */

public class PatientModeCheckinDTO {

    @SerializedName("metadata")
    @Expose
    private PatientModeCheckinMetaDataDTO metaData = new PatientModeCheckinMetaDataDTO();

    @SerializedName("payload")
    @Expose
    private PatientModeCheckinPayloadDTO payload = new PatientModeCheckinPayloadDTO();

    public PatientModeCheckinPayloadDTO getPayload() {
        return payload;
    }

    public void setPayload(PatientModeCheckinPayloadDTO payload) {
        this.payload = payload;
    }

    public PatientModeCheckinMetaDataDTO getMetaData() {
        return metaData;
    }

    public void setMetaData(PatientModeCheckinMetaDataDTO metaData) {
        this.metaData = metaData;
    }
}
