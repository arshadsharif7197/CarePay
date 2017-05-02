package com.carecloud.carepay.practice.library.patientmodecheckin.models;

import com.carecloud.carepaylibray.appointments.models.AppointmentsMetadataDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 4/25/17.
 */

public class CheckinModeDTO {

    @SerializedName("metadata")
    @Expose
    private AppointmentsMetadataDTO metadata = new AppointmentsMetadataDTO();

    public AppointmentsMetadataDTO getMetadata() {
        return metadata;
    }

    public void setMetadata(AppointmentsMetadataDTO metadata) {
        this.metadata = metadata;
    }
}
