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

    public UnifiedSignInPayload getPayload() {
        return payload;
    }

    public void setPayload(UnifiedSignInPayload payload) {
        this.payload = payload;
    }

}
