package com.carecloud.carepay.service.library.unifiedauth;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 3/9/17.
 */

public class UnifiedAuthorizationModel {

    @SerializedName("cognito")
    @Expose
    private UnifiedCognitoInfo cognito = new UnifiedCognitoInfo();

    public UnifiedCognitoInfo getCognito() {
        return cognito;
    }

    public void setCognito(UnifiedCognitoInfo cognito) {
        this.cognito = cognito;
    }
}
