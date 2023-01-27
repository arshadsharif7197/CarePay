package com.carecloud.shamrocksdk.registrations.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Model Class for setting and retrieving Device auth token to connect to Shamrock Payments
 */

public class Authentication {
    @SerializedName("auth_token")
    @Expose
    private String deepstreamAuthToken;

    public String getDeepStreamAuthToken() {
        return deepstreamAuthToken;
    }

    public void setDeepStreamAuthToken(String deepstreamAuthToken) {
        this.deepstreamAuthToken = deepstreamAuthToken;
    }
}
