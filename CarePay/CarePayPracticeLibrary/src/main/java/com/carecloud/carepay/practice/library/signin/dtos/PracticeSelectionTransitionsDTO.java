package com.carecloud.carepay.practice.library.signin.dtos;

import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 3/9/17.
 */

public class PracticeSelectionTransitionsDTO {

    @SerializedName("authenticate")
    @Expose
    private TransitionDTO authenticate = new TransitionDTO();

    public TransitionDTO getAuthenticate() {
        return authenticate;
    }

    public void setAuthenticate(TransitionDTO authenticate) {
        this.authenticate = authenticate;
    }
}
