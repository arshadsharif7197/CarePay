package com.carecloud.carepay.service.library.dtos;

import com.carecloud.carepay.service.library.constants.CognitoConstants;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jahirul Bhuiyan on 10/25/2016.
 */
@Deprecated
public class CognitoDTO {
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
        return  (userPoolId == null || userPoolId.trim().equals(""))? CognitoConstants.USER_POOL_ID: userPoolId;
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
        return (clientId == null || clientId.trim().equals(""))? CognitoConstants.USER_POOL_ID: clientId;
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
