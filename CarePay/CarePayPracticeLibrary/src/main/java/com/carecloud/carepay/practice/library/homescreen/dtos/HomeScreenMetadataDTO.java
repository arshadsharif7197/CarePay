package com.carecloud.carepay.practice.library.homescreen.dtos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jahirul Bhuiyan on 10/27/2016.
 */

public class HomeScreenMetadataDTO {
    @SerializedName("labels")
    @Expose
    private HomeScreenLabelDTO labels;

    @SerializedName("transitions")
    @Expose
    private HomeScreenTransitionsDTO transitions;

    /**
     *
     * @return
     * The labels
     */
    public HomeScreenLabelDTO getLabels() {
        return labels;
    }

    /**
     *
     * @param labels
     * The labels
     */
    public void setLabels(HomeScreenLabelDTO labels) {
        this.labels = labels;
    }

    /**
     *
     * @return
     * The transitions
     */
    public HomeScreenTransitionsDTO getTransitions() {
        return transitions;
    }

    /**
     *
     * @param transitions
     * The transitions
     */
    public void setTransitions(HomeScreenTransitionsDTO transitions) {
        this.transitions = transitions;
    }
}
