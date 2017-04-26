package com.carecloud.carepay.practice.library.patientmodecheckin.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 4/25/17.
 */

public class PatientModeCheckinDTO {

    @SerializedName("payload")
    @Expose
    private PatientModeCheckinPayloadDTO payloadDTO = new PatientModeCheckinPayloadDTO();

    public PatientModeCheckinPayloadDTO getPayloadDTO() {
        return payloadDTO;
    }

    public void setPayloadDTO(PatientModeCheckinPayloadDTO payloadDTO) {
        this.payloadDTO = payloadDTO;
    }
}
