package com.carecloud.carepay.service.library.unifiedauth;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 3/9/17.
 */

public class UnifiedSignInPayload {

    @SerializedName(value = "practice_mode_signin", alternate = {"patient_app_signin"})
    @Expose
    private UnifiedAuthorizationModel authorizationModel = new UnifiedAuthorizationModel();

    @SerializedName("sign_in")
    @Expose
    private UnifiedPatientModeModel signIn = new UnifiedPatientModeModel();

    @SerializedName("badge_counters")
    @Expose
    private BadgeCounter badgeCounter = new BadgeCounter();

    public UnifiedAuthorizationModel getAuthorizationModel() {
        return authorizationModel;
    }

    public UnifiedPatientModeModel getSignIn() {
        return signIn;
    }

    public BadgeCounter getBadgeCounter() {
        return badgeCounter;
    }

    public void setBadgeCounter(BadgeCounter badgeCounter) {
        this.badgeCounter = badgeCounter;
    }
}
