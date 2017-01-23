package com.carecloud.carepay.practice.library.signin.dtos;

/**
 * Created by sudhir_pingale on 11/17/2016.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SignInPatientModeDataModelDTO {
    @SerializedName("login")
    @Expose
    private SignInPatientModeModelDTO login;

    /**
     * @return The login
     */
    public SignInPatientModeModelDTO getLogin() {
        return login;
    }

    /**
     * @param login The login
     */
    public void setLogin(SignInPatientModeModelDTO login) {
        this.login = login;
    }

}
