package com.carecloud.carepaylibray.adhoc;

import com.carecloud.carepaylibray.intake.models.AppointmentMetadataModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author pjohnson on 25/10/17.
 */

public class AdhocFormsPatientModeInfo {

    @Expose
    @SerializedName("metadata")
    private AppointmentMetadataModel metadata = new AppointmentMetadataModel();

    public AppointmentMetadataModel getMetadata() {
        return metadata;
    }

    public void setMetadata(AppointmentMetadataModel metadata) {
        this.metadata = metadata;
    }

}
