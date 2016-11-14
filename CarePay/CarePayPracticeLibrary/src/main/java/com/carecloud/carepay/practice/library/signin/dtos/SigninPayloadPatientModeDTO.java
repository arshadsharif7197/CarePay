package com.carecloud.carepay.practice.library.signin.dtos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by prem_mourya on 11/11/2016.
 */

public class SigninPayloadPatientModeDTO {

    @SerializedName("cognito")
    @Expose
    private SigninPatientModeCognitoDTO cognito;

    /**
     *
     * @return
     * The cognito
     */
    public SigninPatientModeCognitoDTO getCognito() {
        return cognito;
    }

    /**
     *
     * @param cognito
     * The cognito
     */
    public void setCognito(SigninPatientModeCognitoDTO cognito) {
        this.cognito = cognito;
    }
}
