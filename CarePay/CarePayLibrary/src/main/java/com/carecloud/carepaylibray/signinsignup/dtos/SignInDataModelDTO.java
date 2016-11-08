package com.carecloud.carepaylibray.signinsignup.dtos;

/**
 * Created by Rahul on 11/7/16.
 */

import com.carecloud.carepaylibray.signinsignup.dtos.signin.SignInDTO;
import com.carecloud.carepaylibray.signinsignup.dtos.signup.SignUpDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SignInDataModelDTO {

    @SerializedName("signin")
    @Expose
    private SignInDTO signin;
    @SerializedName("signup")
    @Expose
    private SignUpDTO signup;

    /**
     * @return The signin
     */
    public SignInDTO getSignin() {
        return signin;
    }

    /**
     * @param signin The signin
     */
    public void setSignin(SignInDTO signin) {
        this.signin = signin;
    }

    /**
     * @return The signup
     */
    public SignUpDTO getSignup() {
        return signup;
    }

    /**
     * @param signup The signup
     */
    public void setSignup(SignUpDTO signup) {
        this.signup = signup;
    }

}
