package com.carecloud.carepay.practice.library.checkin.dtos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jahirul Bhuiyan on 10/27/2016.
 */

public class CheckInMetadataDTO {

    @SerializedName("labels")
    @Expose
    private CheckInLabelDTO label;
    @SerializedName("transitions")
    @Expose
    private CheckInTransitionsDTO transitions;

    /**
     *
     * @return
     * The label
     */
    public CheckInLabelDTO getLabel() {
        return label;
    }

    /**
     *
     * @param label
     * The label
     */
    public void setLabel(CheckInLabelDTO label) {
        this.label = label;
    }

    /**
     *
     * @return
     * The transitions
     */
    public CheckInTransitionsDTO getTransitions() {
        return transitions;
    }

    /**
     *
     * @param transitions
     * The transitions
     */
    public void setTransitions(CheckInTransitionsDTO transitions) {
        this.transitions = transitions;
    }
}

