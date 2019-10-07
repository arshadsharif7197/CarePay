package com.carecloud.carepay.service.library.unifiedauth;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author pjohnson on 3/21/19.
 */
public class RefreshTokenPayload {

    @SerializedName(value = "practice_mode_signin", alternate = {"patient_app_signin"})
    @Expose
    private RefreshAuthorizationModel authorizationModel = new RefreshAuthorizationModel();

    public RefreshAuthorizationModel getAuthorizationModel() {
        return authorizationModel;
    }

    public void setAuthorizationModel(RefreshAuthorizationModel authorizationModel) {
        this.authorizationModel = authorizationModel;
    }
}
