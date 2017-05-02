package com.carecloud.carepay.practice.library.signin.dtos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 3/9/17.
 */

public class PracticeSelectionMetadata {

    @SerializedName("transitions")
    @Expose
    private PracticeSelectionTransitionsDTO transitions = new PracticeSelectionTransitionsDTO();

    public PracticeSelectionTransitionsDTO getTransitions() {
        return transitions;
    }

    public void setTransitions(PracticeSelectionTransitionsDTO transitions) {
        this.transitions = transitions;
    }
}
