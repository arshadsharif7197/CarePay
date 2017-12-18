package com.carecloud.carepay.practice.library.adhocforms;

import com.carecloud.carepaylibray.appointments.models.AppointmentMetadataModel;
import com.carecloud.carepaylibray.interfaces.DTO;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 12/15/17
 */

public class AdHocFormsModel implements DTO {

    @SerializedName("metadata")
    private AppointmentMetadataModel metadata;

    @SerializedName("payload")
    private AdHocFormsPayload payload;

    @SerializedName("state")
    private String state;

    public AppointmentMetadataModel getMetadata() {
        return metadata;
    }

    public void setMetadata(AppointmentMetadataModel metadata) {
        this.metadata = metadata;
    }

    public AdHocFormsPayload getPayload() {
        return payload;
    }

    public void setPayload(AdHocFormsPayload payload) {
        this.payload = payload;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
