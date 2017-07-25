package com.carecloud.carepay.patient.patientsplash.dtos;

/**
 * Created by Rahul on 11/7/16.
 */


import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TransitionsDTO {

    @SerializedName("signin")
    @Expose
    private TransitionDTO signin = new TransitionDTO();

    @SerializedName("logout")
    @Expose
    private TransitionDTO logout = new TransitionDTO();

    /**
     * @return The signin
     */
    public TransitionDTO getSignin() {
        return signin;
    }

    /**
     * @param signin The signin
     */
    public void setSignin(TransitionDTO signin) {
        this.signin = signin;
    }

    public TransitionDTO getLogout() {
        return logout;
    }

    public void setLogout(TransitionDTO logout) {
        this.logout = logout;
    }
}