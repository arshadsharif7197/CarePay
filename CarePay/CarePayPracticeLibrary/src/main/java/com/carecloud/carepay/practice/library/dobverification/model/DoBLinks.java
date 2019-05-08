package com.carecloud.carepay.practice.library.dobverification.model;

import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author pjohnson on 4/4/19.
 */
public class DoBLinks {

    @SerializedName(value = "language_metadata", alternate = "language")
    @Expose
    private TransitionDTO language = new TransitionDTO();
    @SerializedName("pinpad")
    @Expose
    private TransitionDTO pinpad = new TransitionDTO();

    public TransitionDTO getLanguage() {
        return language;
    }

    public void setLanguage(TransitionDTO language) {
        this.language = language;
    }

    public TransitionDTO getPinpad() {
        return pinpad;
    }

    public void setPinpad(TransitionDTO pinpad) {
        this.pinpad = pinpad;
    }
}
