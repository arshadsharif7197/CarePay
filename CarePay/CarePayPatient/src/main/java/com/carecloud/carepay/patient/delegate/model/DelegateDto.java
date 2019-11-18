package com.carecloud.carepay.patient.delegate.model;

import com.carecloud.carepaylibray.interfaces.DTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author pjohnson on 2019-06-13.
 */
public class DelegateDto implements DTO {

    @SerializedName("payload")
    @Expose
    private DelegatePayload payload = new DelegatePayload();

    @SerializedName("metadata")
    @Expose
    private DelegateMetadata metadata = new DelegateMetadata();

    public DelegatePayload getPayload() {
        return payload;
    }

    public void setPayload(DelegatePayload payload) {
        this.payload = payload;
    }

    public DelegateMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(DelegateMetadata metadata) {
        this.metadata = metadata;
    }
}
