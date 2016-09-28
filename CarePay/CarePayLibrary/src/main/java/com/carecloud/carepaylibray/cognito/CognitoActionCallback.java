package com.carecloud.carepaylibray.cognito;

/**
 * Created by lsoco_user on 9/28/2016.
 * An interface that defines a method to be executed upon a succesful signin or signup
 */

public interface CognitoActionCallback {
    void executeAction();
}
