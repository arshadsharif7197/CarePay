package com.carecloud.carepay.patient.retail.models.sso;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 10/30/17
 */

public class SsoModel {

    @SerializedName("appClientId")
    private String appId;

    @SerializedName("userId")
    private String userId;

    @SerializedName("profile")
    private Profile profile;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }
}
