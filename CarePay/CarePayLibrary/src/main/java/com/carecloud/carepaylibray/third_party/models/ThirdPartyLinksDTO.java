package com.carecloud.carepaylibray.third_party.models;

import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.google.gson.annotations.SerializedName;

public class ThirdPartyLinksDTO {

    @SerializedName("continue")
    private TransitionDTO continueTransition = new TransitionDTO();

    public TransitionDTO getContinueTransition() {
        return continueTransition;
    }

    public void setContinueTransition(TransitionDTO continueTransition) {
        this.continueTransition = continueTransition;
    }
}
