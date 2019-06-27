package com.carecloud.carepay.patient.delegate;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author pjohnson on 2019-06-27.
 */
class DelegateMetadata {

    @Expose
    @SerializedName("transitions")
    private DelegateTransitions transitions = new DelegateTransitions();

    public DelegateTransitions getTransitions() {
        return transitions;
    }

    public void setTransitions(DelegateTransitions transitions) {
        this.transitions = transitions;
    }
}
