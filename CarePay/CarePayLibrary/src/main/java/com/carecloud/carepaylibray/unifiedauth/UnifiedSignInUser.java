package com.carecloud.carepaylibray.unifiedauth;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 3/8/17.
 */

public class UnifiedSignInUser {

    @SerializedName("password")
    @Expose
    private String password;

    @SerializedName("alias")
    @Expose
    private String email;

    @SerializedName("device_token")
    @Expose
    private String deviceToken;

    @SerializedName("device_type")
    @Expose
    private String deviceType = "android";


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getDeviceType() {
        return deviceType;
    }
}
