package com.carecloud.carepaylibray.adhoc;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author pjohnson on 25/10/17.
 */

public class AdhocFormsPatientModeInfo {

    @Expose
    @SerializedName("metadata")
    private AdhocFormsPatientModeInfoMetadata metadata = new AdhocFormsPatientModeInfoMetadata();

    public AdhocFormsPatientModeInfoMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(AdhocFormsPatientModeInfoMetadata metadata) {
        this.metadata = metadata;
    }

}
