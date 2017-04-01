package com.carecloud.carepay.service.library.unifiedauth;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 3/9/17.
 */

public class UnifiedSignInPayload {

    @SerializedName(value="practice_mode_signin", alternate={"patient_app_signin"})
    @Expose
    private UnifiedAuthorizationModel authorizationModel = new UnifiedAuthorizationModel();

    @SerializedName("sign_in")
    @Expose
    private UnifiedPatientModeModel signIn = new UnifiedPatientModeModel();

    public UnifiedAuthorizationModel getAuthorizationModel() {
        return authorizationModel;
    }

    public UnifiedPatientModeModel getSignIn() {
        return signIn;
    }
}
