package com.carecloud.carepay.service.library.unifiedauth;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 3/9/17.
 */

public class UnifiedCognitoInfo {

    @SerializedName("authentication")
    @Expose
    private UnifiedAuthenticationTokens authenticationTokens = new UnifiedAuthenticationTokens();

    public UnifiedAuthenticationTokens getAuthenticationTokens() {
        return authenticationTokens;
    }

    public void setAuthenticationTokens(UnifiedAuthenticationTokens authenticationTokens) {
        this.authenticationTokens = authenticationTokens;
    }
}
