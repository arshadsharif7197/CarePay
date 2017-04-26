package com.carecloud.carepay.practice.library.signin.dtos;

/**
 * Created by sudhir_pingale on 11/18/2016.
 */

import com.carecloud.carepay.service.library.dtos.CognitoDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
@Deprecated
public class PatientModeSigninDataDTO {

    @SerializedName("cognito")
    @Expose
    private CognitoDTO cognito = new CognitoDTO();

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
