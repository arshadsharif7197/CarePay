package com.carecloud.carepay.patient.patientsplash.dtos;

/**
 * Created by Rahul on 11/7/16.
 */


import com.google.gson.annotations.Expose;
        import com.google.gson.annotations.SerializedName;

public class TransitionsDTO {

    @SerializedName("signin")
    @Expose
    private SignInLikDTO signin;

    /**
     *
     * @return
     * The signin
     */
    public SignInLikDTO getSignin() {
        return signin;
    }

    /**
     *
     * @param signin
     * The signin
     */
    public void setSignin(SignInLikDTO signin) {
        this.signin = signin;
    }

}