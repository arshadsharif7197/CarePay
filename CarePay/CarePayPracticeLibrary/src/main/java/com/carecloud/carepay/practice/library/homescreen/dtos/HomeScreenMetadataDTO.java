package com.carecloud.carepay.practice.library.homescreen.dtos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jahirul Bhuiyan on 10/27/2016.
 */

public class HomeScreenMetadataDTO {
    @SerializedName("label")
    @Expose
    private HomeScreenLabelDTO label;

    @SerializedName("transitions")
    @Expose
    private HomeScreenTransitionsDTO transitions;

    /**
     *
     * @return
     * The label
     */
    public HomeScreenLabelDTO getLabel() {
        return label;
    }

    /**
     *
     * @param label
     * The label
     */
    public void setLabel(HomeScreenLabelDTO label) {
        this.label = label;
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
