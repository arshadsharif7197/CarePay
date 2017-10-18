package com.carecloud.carepaylibray.payments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MerchantServiceMetadataDTO {
    @SerializedName("api_key")
    @Expose
    private String apiKey;
    @SerializedName("api_secret")
    @Expose
    private String apiSecret;
    @SerializedName("android_public_key")
    @Expose
    private String androidPublicKey;
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
    @SerializedName("token_type")
    @Expose
    private String tokenType;
    @SerializedName("api_endpoint")
    @Expose
    private String baseUrl;
    @SerializedName("tokenization_path")
    @Expose
    private String urlPath;
    @SerializedName("tokenization_auth")
    @Expose
    private String tokenizationAuth;

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

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getUrlPath() {
        return urlPath;
    }

    public void setUrlPath(String urlPath) {
        this.urlPath = urlPath;
    }

    public String getTokenizationAuth() {
        return tokenizationAuth;
    }

    public void setTokenizationAuth(String tokenizationAuth) {
        this.tokenizationAuth = tokenizationAuth;
    }
}
