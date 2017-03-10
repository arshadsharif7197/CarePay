package com.carecloud.carepay.practice.library.signin.dtos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 3/9/17.
 */

public class PracticeSelectionMetadata {

    @SerializedName("labels")
    @Expose
    private PracticeSelectionLabels labels = new PracticeSelectionLabels();

    @SerializedName("transitions")
    @Expose
    private PracticeSelectionTransitions transitions = new PracticeSelectionTransitions();

    public PracticeSelectionTransitions getTransitions() {
        return transitions;
    }

    public void setTransitions(PracticeSelectionTransitions transitions) {
        this.transitions = transitions;
    }

    public PracticeSelectionLabels getLabels() {
        return labels;
    }

    public void setLabels(PracticeSelectionLabels labels) {
        this.labels = labels;
    }
}
