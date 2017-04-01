package com.carecloud.carepay.service.library.unifiedauth;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 3/10/17.
 */

public class UnifiedPatientModeModel {

    @SerializedName("metadata")
    @Expose
    private UnifiedPatientModeMetadata metadata = new UnifiedPatientModeMetadata();

    public UnifiedPatientModeMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(UnifiedPatientModeMetadata metadata) {
        this.metadata = metadata;
    }
}