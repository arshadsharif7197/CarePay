package com.carecloud.carepay.practice.library.signin.dtos;

import com.carecloud.carepay.service.library.dtos.CognitoDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jahirul Bhuiyan on 10/25/2016.
 */

public class PracticeModeSigninDTO {
    @SerializedName("language")
    @Expose
    private SigninLanguageDTO language;
    @SerializedName("cognito")
    @Expose
    private CognitoDTO cognito;

    /**
     *
     * @return
     * The language
     */
    public SigninLanguageDTO getLanguage() {
        return language;
    }

    /**
     *
     * @param language
     * The language
     */
    public void setLanguage(SigninLanguageDTO language) {
        this.language = language;
    }

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
