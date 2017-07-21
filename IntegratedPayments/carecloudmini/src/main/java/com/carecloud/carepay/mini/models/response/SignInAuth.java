package com.carecloud.carepay.mini.models.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 6/27/17
 */

public class SignInAuth {

//    @SerializedName("cognito")
    @SerializedName("body")
    private Cognito cognito = new Cognito();

    public Cognito getCognito() {
        return cognito;
    }

    public void setCognito(Cognito cognito) {
        this.cognito = cognito;
    }

    public class Cognito {

//        @SerializedName("authentication")
        @SerializedName("AuthenticationResult")
        private Authentication authentication = new Authentication();

        public Authentication getAuthentication() {
            return authentication;
        }

        public void setAuthentication(Authentication authentication) {
            this.authentication = authentication;
        }

    }
}
