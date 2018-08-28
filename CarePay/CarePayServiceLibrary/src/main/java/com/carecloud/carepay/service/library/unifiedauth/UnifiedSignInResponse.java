package com.carecloud.carepay.service.library.unifiedauth;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 3/9/17.
 */

public class UnifiedSignInResponse {

    @SerializedName("payload")
    @Expose
    private UnifiedSignInPayload payload = new UnifiedSignInPayload();

    @SerializedName("metadata")
    @Expose
    private UnifiedSignInResponseMetadata metadata = new UnifiedSignInResponseMetadata();

    public UnifiedSignInPayload getPayload() {
        return payload;
    }

    public void setPayload(UnifiedSignInPayload payload) {
        this.payload = payload;
    }

    public UnifiedSignInResponseMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(UnifiedSignInResponseMetadata metadata) {
        this.metadata = metadata;
    }
}
