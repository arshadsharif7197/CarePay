package com.carecloud.carepaylibray.signinsignup.dtos.unifiedauth;

import com.carecloud.carepaylibray.signinsignup.dtos.SignInMetaDataDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 3/9/17.
 */

public class UnifiedSignInResponse {

    @SerializedName("metadata")
    @Expose
    private SignInMetaDataDTO metadata;

    @SerializedName("payload")
    @Expose
    private UnifiedSignInPayload payload = new UnifiedSignInPayload();


    public UnifiedSignInPayload getPayload() {
        return payload;
    }

    public void setPayload(UnifiedSignInPayload payload) {
        this.payload = payload;
    }

    public SignInMetaDataDTO getMetadata() {
        return metadata;
    }

    public void setMetadata(SignInMetaDataDTO metadata) {
        this.metadata = metadata;
    }
}
