package com.carecloud.carepay.patient.myhealth.dtos;

import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author pjohnson on 6/12/17.
 */

public class MyHealthTransitionsDto {

    @SerializedName("signin")
    @Expose
    private TransitionDTO signin = new TransitionDTO();

    @SerializedName("logout")
    @Expose
    private TransitionDTO logout = new TransitionDTO();

    public TransitionDTO getSignin() {
        return signin;
    }

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
