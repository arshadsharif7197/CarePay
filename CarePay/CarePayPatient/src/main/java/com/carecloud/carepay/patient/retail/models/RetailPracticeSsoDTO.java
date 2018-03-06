package com.carecloud.carepay.patient.retail.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 1/11/18
 */

public class RetailPracticeSsoDTO {

    @SerializedName("encoded")
    private String encodedProfile;

    @SerializedName("timestamp")
    private String timestamp;

    @SerializedName("signature")
    private String signature;

    /**
     * get the full sso parameter
     * @return sso parameter
     */
    public String getSSO(){
        return getEncodedProfile() + ' '+
                getSignature() + ' ' +
                getTimestamp();
    }

    public String getEncodedProfile() {
        return encodedProfile;
    }

    public void setEncodedProfile(String encodedProfile) {
        this.encodedProfile = encodedProfile;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
