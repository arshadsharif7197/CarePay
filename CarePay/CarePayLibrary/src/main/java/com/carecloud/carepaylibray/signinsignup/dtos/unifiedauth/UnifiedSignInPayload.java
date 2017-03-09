package com.carecloud.carepaylibray.signinsignup.dtos.unifiedauth;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 3/9/17.
 */

public class UnifiedSignInPayload {

    @SerializedName("patient_app_signin")
    @Expose
    private UnifiedAuthorizationModel authModel = new UnifiedAuthorizationModel();

    public UnifiedAuthorizationModel getAuthModel() {
        return authModel;
    }

    public void setAuthModel(UnifiedAuthorizationModel authModel) {
        this.authModel = authModel;
    }
}
