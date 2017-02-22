package com.carecloud.carepay.practice.library.signin.dtos;

/**
 * Created by Rahul on 11/2/16.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SignInDataModelDTO {
    @SerializedName("signin")
    @Expose
    private SignInModelDTO signin = new SignInModelDTO();

    /**
     * @return The signin
     */
    public SignInModelDTO getSignin() {
        return signin;
    }

    /**
     * @param signin The signin
     */
    public void setSignin(SignInModelDTO signin) {
        this.signin = signin;
    }

}
