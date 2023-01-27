package com.carecloud.shamrocksdk.registrations.interfaces;

import com.clover.sdk.util.CloverAuth;

/**
 * Abstract adapter class for allowing clients to implement {@link AccountInfoCallback} methods selectively
 */

public abstract class AccountInfoAdapter implements AccountInfoCallback {

    @Override
    public void onRetrieveAccountInfo(CloverAuth.AuthResult authResult) {

    }

    @Override
    public void onRetrieveMerchantId(String merchantId) {

    }

    @Override
    public void onRetrieveAppId(String appId) {

    }

    @Override
    public void onRetrieveAuthToken(String authToken) {

    }

}
