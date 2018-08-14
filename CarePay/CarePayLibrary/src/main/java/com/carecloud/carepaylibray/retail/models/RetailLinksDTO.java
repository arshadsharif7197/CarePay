package com.carecloud.carepaylibray.retail.models;

import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 11/20/17
 */

public class RetailLinksDTO {

    @SerializedName("retail")
    private TransitionDTO retail = new TransitionDTO();

    @SerializedName(value = "language_metadata", alternate = "language")
    private TransitionDTO language = new TransitionDTO();

    public TransitionDTO getRetail() {
        return retail;
    }

    public void setRetail(TransitionDTO retail) {
        this.retail = retail;
    }

    public TransitionDTO getLanguage() {
        return language;
    }

    public void setLanguage(TransitionDTO language) {
        this.language = language;
    }
}
