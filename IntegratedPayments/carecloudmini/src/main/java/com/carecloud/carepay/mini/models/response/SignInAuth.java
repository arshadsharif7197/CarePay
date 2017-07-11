package com.carecloud.carepay.mini.models.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 6/27/17
 */

public class SignInAuth {

    @SerializedName("cognito")
    private Cognito cognito = new Cognito();

    public Cognito getCognito() {
        return cognito;
    }

    public void setCognito(Cognito cognito) {
        this.cognito = cognito;
    }

    public class Cognito {

        @SerializedName("authentication")
        private Authentication authentication = new Authentication();

        public Authentication getAuthentication() {
            return authentication;
        }

        public void setAuthentication(Authentication authentication) {
            this.authentication = authentication;
        }

        public class Authentication {

            @SerializedName("AccessToken")
            private String accessToken;

            @SerializedName("RefreshToken")
            private String refreshToken;

            @SerializedName("IdToken")
            private String idToken;

            public String getAccessToken() {
                return accessToken;
            }

            public void setAccessToken(String accessToken) {
                this.accessToken = accessToken;
            }

            public String getRefreshToken() {
                return refreshToken;
            }

            public void setRefreshToken(String refreshToken) {
                this.refreshToken = refreshToken;
            }

            public String getIdToken() {
                return idToken;
            }

            public void setIdToken(String idToken) {
                this.idToken = idToken;
            }
        }
    }
}
