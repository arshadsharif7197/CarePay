package com.carecloud.carepay.service.library.unifiedauth;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 3/9/17.
 */

public class UnifiedSignInPayload {

    @SerializedName("patient_app_signin")
    @Expose
    private UnifiedAuthorizationModel patientAppAuth = new UnifiedAuthorizationModel();

    @SerializedName("practice_mode_signin")
    @Expose
    private UnifiedAuthorizationModel practiceModeAuth = new UnifiedAuthorizationModel();

    @SerializedName("sign_in")
    @Expose
    private UnifiedPatientModeModel signIn = new UnifiedPatientModeModel();

    public UnifiedAuthorizationModel getPatientAppAuth() {
        return patientAppAuth;
    }

    public void setPatientAppAuth(UnifiedAuthorizationModel patientAppAuth) {
        this.patientAppAuth = patientAppAuth;
    }

    public UnifiedAuthorizationModel getPracticeModeAuth() {
        return practiceModeAuth;
    }

    public void setPracticeModeAuth(UnifiedAuthorizationModel practiceModeAuth) {
        this.practiceModeAuth = practiceModeAuth;
    }

    public UnifiedPatientModeModel getSignIn() {
        return signIn;
    }

    public void setSignIn(UnifiedPatientModeModel signIn) {
        this.signIn = signIn;
    }
}
