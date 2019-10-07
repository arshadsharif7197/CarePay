package com.carecloud.carepay.service.library.unifiedauth;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author pjohnson on 3/21/19.
 */
public class RefreshTokenDto {

    @SerializedName("payload")
    @Expose
    private RefreshTokenPayload payload = new RefreshTokenPayload();

    public RefreshTokenPayload getPayload() {
        return payload;
    }

    public void setPayload(RefreshTokenPayload payload) {
        this.payload = payload;
    }
}
