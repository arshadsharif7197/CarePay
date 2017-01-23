package com.carecloud.carepay.practice.library.signin.dtos;

import com.carecloud.carepay.service.library.dtos.CognitoDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jahirul Bhuiyan on 10/25/2016.
 */

public class PracticeModeSigninDTO {

    @SerializedName("cognito")
    @Expose
    private CognitoDTO cognito;

    /**
     *
     * @return
     * The cognito
     */
    public CognitoDTO getCognito() {
        return cognito;
    }

    /**
     *
     * @param cognito
     * The cognito
     */
    public void setCognito(CognitoDTO cognito) {
        this.cognito = cognito;
    }

}
