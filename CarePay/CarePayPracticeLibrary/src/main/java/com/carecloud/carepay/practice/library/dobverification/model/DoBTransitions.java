package com.carecloud.carepay.practice.library.dobverification.model;

import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author pjohnson on 4/4/19.
 */
public class DoBTransitions {

    @SerializedName("practice_mode")
    @Expose
    private TransitionDTO practiceMode = new TransitionDTO();

    public TransitionDTO getPracticeMode() {
        return practiceMode;
    }

    public void setPracticeMode(TransitionDTO practiceMode) {
        this.practiceMode = practiceMode;
    }
}
