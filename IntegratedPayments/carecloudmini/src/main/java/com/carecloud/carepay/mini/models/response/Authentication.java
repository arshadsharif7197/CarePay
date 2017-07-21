package com.carecloud.carepay.mini.models.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 7/20/17
 */

public class Authentication {

//    @SerializedName("AccessToken")
    @SerializedName("access_token")
    private String accessToken;

//    @SerializedName("RefreshToken")
    @SerializedName("refresh_token")
    private String refreshToken;

//    @SerializedName("IdToken")
    @SerializedName("id_token")
    private String idToken;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

}
