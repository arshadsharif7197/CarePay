package com.carecloud.carepay.practice.library.signin.dtos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jahirul Bhuiyan on 10/25/2016.
 */

public class SigninPayloadDTO {
    @SerializedName("practice_mode_signin")
    @Expose
    private PracticeModeSigninDTO practiceModeSignin;

    /**
     *
     * @return
     * The practiceModeSignin
     */
    public PracticeModeSigninDTO getPracticeModeSignin() {
        return practiceModeSignin;
    }

    /**
     *
     * @param practiceModeSignin
     * The practice_mode_signin
     */
    public void setPracticeModeSignin(PracticeModeSigninDTO practiceModeSignin) {
        this.practiceModeSignin = practiceModeSignin;
    }
}
