package com.carecloud.carepaylibray.demographicsettings.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DemographicsSettingsPapiMetadataMerchantServiceDTO {
    @SerializedName("api_key")
    @Expose
    private String apiKey;
    @SerializedName("api_secret")
    @Expose
    private String apiSecret;
    @SerializedName("ios_public_key")
    @Expose
    private String iosPublicKey;
    @SerializedName("android_public_key")
    @Expose
    private String androidPublicKey;
    @SerializedName("ios_public_key_hash")
    @Expose
    private String iosPublicKeyHash;
    @SerializedName("android_public_key_hash")
    @Expose
    private String androidPublicKeyHash;
    @SerializedName("master_merchant_token")
    @Expose
    private String masterMerchantToken;
    @SerializedName("master_js_security_key")
    @Expose
    private String masterJsSecurityKey;
    @SerializedName("master_ta_token")
    @Expose
    private String masterTaToken;

    /**
     * Gets api key.
     *
     * @return the api key
     */
    public String getApiKey() {
        return apiKey;
    }

    /**
     * Sets api key.
     *
     * @param apiKey the api key
     */
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    /**
     * Gets api secret.
     *
     * @return the api secret
     */
    public String getApiSecret() {
        return apiSecret;
    }

    /**
     * Sets api secret.
     *
     * @param apiSecret the api secret
     */
    public void setApiSecret(String apiSecret) {
        this.apiSecret = apiSecret;
    }

    /**
     * Gets ios public key.
     *
     * @return the ios public key
     */
    public String getIosPublicKey() {
        return iosPublicKey;
    }

    /**
     * Sets ios public key.
     *
     * @param iosPublicKey the ios public key
     */
    public void setIosPublicKey(String iosPublicKey) {
        this.iosPublicKey = iosPublicKey;
    }

    /**
     * Gets android public key.
     *
     * @return the android public key
     */
    public String getAndroidPublicKey() {
        return androidPublicKey;
    }

    /**
     * Sets android public key.
     *
     * @param androidPublicKey the android public key
     */
    public void setAndroidPublicKey(String androidPublicKey) {
        this.androidPublicKey = androidPublicKey;
    }

    /**
     * Gets ios public key hash.
     *
     * @return the ios public key hash
     */
    public String getIosPublicKeyHash() {
        return iosPublicKeyHash;
    }

    /**
     * Sets ios public key hash.
     *
     * @param iosPublicKeyHash the ios public key hash
     */
    public void setIosPublicKeyHash(String iosPublicKeyHash) {
        this.iosPublicKeyHash = iosPublicKeyHash;
    }

    /**
     * Gets android public key hash.
     *
     * @return the android public key hash
     */
    public String getAndroidPublicKeyHash() {
        return androidPublicKeyHash;
    }

    /**
     * Sets android public key hash.
     *
     * @param androidPublicKeyHash the android public key hash
     */
    public void setAndroidPublicKeyHash(String androidPublicKeyHash) {
        this.androidPublicKeyHash = androidPublicKeyHash;
    }

    /**
     * Gets master merchant token.
     *
     * @return the master merchant token
     */
    public String getMasterMerchantToken() {
        return masterMerchantToken;
    }

    /**
     * Sets master merchant token.
     *
     * @param masterMerchantToken the master merchant token
     */
    public void setMasterMerchantToken(String masterMerchantToken) {
        this.masterMerchantToken = masterMerchantToken;
    }

    /**
     * Gets master js security key.
     *
     * @return the master js security key
     */
    public String getMasterJsSecurityKey() {
        return masterJsSecurityKey;
    }

    /**
     * Sets master js security key.
     *
     * @param masterJsSecurityKey the master js security key
     */
    public void setMasterJsSecurityKey(String masterJsSecurityKey) {
        this.masterJsSecurityKey = masterJsSecurityKey;
    }

    /**
     * Gets master ta token.
     *
     * @return the master ta token
     */
    public String getMasterTaToken() {
        return masterTaToken;
    }

    /**
     * Sets master ta token.
     *
     * @param masterTaToken the master ta token
     */
    public void setMasterTaToken(String masterTaToken) {
        this.masterTaToken = masterTaToken;
    }
}
