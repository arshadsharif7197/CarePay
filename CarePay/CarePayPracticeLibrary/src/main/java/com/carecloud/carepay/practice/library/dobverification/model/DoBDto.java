package com.carecloud.carepay.practice.library.dobverification.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author pjohnson on 4/4/19.
 */
public class DoBDto {

    @SerializedName("metadata")
    @Expose
    private DoBMetadata metadata = new DoBMetadata();
    @SerializedName("payload")
    @Expose
    private DoBPayload payload = new DoBPayload();

    public DoBMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(DoBMetadata metadata) {
        this.metadata = metadata;
    }

    public DoBPayload getPayload() {
        return payload;
    }

    public void setPayload(DoBPayload payload) {
        this.payload = payload;
    }
}
