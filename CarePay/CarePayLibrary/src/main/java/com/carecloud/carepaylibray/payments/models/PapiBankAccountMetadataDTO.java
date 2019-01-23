package com.carecloud.carepaylibray.payments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PapiBankAccountMetadataDTO {
    @SerializedName("ein")
    @Expose
    private String ein;
    @SerializedName("ta_token")
    @Expose
    private String taToken;
    @SerializedName("js_security_key")
    @Expose
    private String jsSecurityKey;
    @SerializedName("mid")
    @Expose
    private String mid;

    /**
     * Gets ein.
     *
     * @return the ein
     */
    public String getEin() {
        return ein;
    }

    /**
     * Sets ein.
     *
     * @param ein the ein
     */
    public void setEin(String ein) {
        this.ein = ein;
    }

    /**
     * Gets ta token.
     *
     * @return the ta token
     */
    public String getTaToken() {
        return taToken;
    }

    /**
     * Sets ta token.
     *
     * @param taToken the ta token
     */
    public void setTaToken(String taToken) {
        this.taToken = taToken;
    }

    /**
     * Gets js security key.
     *
     * @return the js security key
     */
    public String getJsSecurityKey() {
        return jsSecurityKey;
    }

    /**
     * Sets js security key.
     *
     * @param jsSecurityKey the js security key
     */
    public void setJsSecurityKey(String jsSecurityKey) {
        this.jsSecurityKey = jsSecurityKey;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }
}
