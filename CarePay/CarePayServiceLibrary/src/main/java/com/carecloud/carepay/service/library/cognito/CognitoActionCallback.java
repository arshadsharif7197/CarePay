package com.carecloud.carepay.service.library.cognito;

/**
 * Created by Jahirul Bhuiyan on 10/12/2016.
 */

public interface CognitoActionCallback {

    void onBeforeLogin();

    void onLoginSuccess();

    void onLoginFailure(String exceptionMessage);
}
