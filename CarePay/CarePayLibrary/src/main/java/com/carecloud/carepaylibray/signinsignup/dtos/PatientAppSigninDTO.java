package com.carecloud.carepaylibray.signinsignup.dtos;

/**
 * Created by Rahul on 11/7/16.
 */


import com.carecloud.carepay.service.library.dtos.CognitoDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class PatientAppSigninDTO {

    @SerializedName("cognito")
    @Expose
    private CognitoDTO cognito;

    /**
     * @return The cognito
     */
    public CognitoDTO getCognito() {
        return cognito;
    }

    /**
     * @param cognito The cognito
     */
    public void setCognito(CognitoDTO cognito) {
        this.cognito = cognito;
    }

}