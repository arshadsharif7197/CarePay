package com.carecloud.carepaylibray.retail.models;

import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 11/20/17
 */

public class RetailTransitionsDTO {

    @SerializedName("logout")
    @Expose
    private TransitionDTO logout = new TransitionDTO();

    public TransitionDTO getLogout() {
        return logout;
    }

    public void setLogout(TransitionDTO logout) {
        this.logout = logout;
    }
}
