package com.carecloud.carepay.practice.library.signin.dtos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by prem_mourya on 11/11/2016.
 */

public class SigninPatientModeCognitoDTO {

    @SerializedName("region")
    @Expose
    private String region;
    @SerializedName("userPoolId")
    @Expose
    private String userPoolId;
    @SerializedName("clientId")
    @Expose
    private String clientId;

    /**
     *
     * @return
     * The region
     */
    public String getRegion() {
        return region;
    }

    /**
     *
     * @param region
     * The region
     */
    public void setRegion(String region) {
        this.region = region;
    }

    /**
     *
     * @return
     * The userPoolId
     */
    public String getUserPoolId() {
        return userPoolId;
    }

    /**
     *
     * @param userPoolId
     * The userPoolId
     */
    public void setUserPoolId(String userPoolId) {
        this.userPoolId = userPoolId;
    }

    /**
     *
     * @return
     * The clientId
     */
    public String getClientId() {
        return clientId;
    }

    /**
     *
     * @param clientId
     * The clientId
     */
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
}
