package com.carecloud.carepay.patient.payment.androidPay;

/**
 * Created by kkannan on 12/17/16.
 */

/**
 * First Data constants related to a specific environment
 */
public interface FirstDatEnvironmentProperties {
    String getEnvName();

    String getUrl();

    String getApiKey();

    String getToken();

    String getPublicKey();

    String getPublicKeyHash();

    String getApiSecret();
}
