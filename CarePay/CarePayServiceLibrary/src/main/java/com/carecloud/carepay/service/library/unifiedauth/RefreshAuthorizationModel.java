package com.carecloud.carepay.service.library.unifiedauth;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author pjohnson on 3/21/19.
 */
public class RefreshAuthorizationModel {

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
