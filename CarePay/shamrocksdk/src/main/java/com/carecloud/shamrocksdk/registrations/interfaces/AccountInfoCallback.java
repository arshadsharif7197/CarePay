package com.carecloud.shamrocksdk.registrations.interfaces;

import com.clover.sdk.util.CloverAuth;

/**
 * Handles retrieving Clover device info required for registering Clover device with Shamrock Payments
 */

public interface AccountInfoCallback {

    /**
     * Called when {@link com.clover.sdk.util.CloverAuth.AuthResult Auth Result} is ready
     * @param authResult
     */
    void onRetrieveAccountInfo(CloverAuth.AuthResult authResult);

    /**
     * Called when merchant id is obtained
     * @param merchantId
     */
    void onRetrieveMerchantId(String merchantId);

    /**
     * Called when app id is obtained
     * @param appId
     */
    void onRetrieveAppId(String appId);

    /**
     * Called when auth token is obtained.
     * @param authToken
     */
    void onRetrieveAuthToken(String authToken);

    /**
     * Called when error is encountered while connecting to Clover Account
     * @param errorMessage
     */
    void onAccountConnectionFailure(String errorMessage);
}
