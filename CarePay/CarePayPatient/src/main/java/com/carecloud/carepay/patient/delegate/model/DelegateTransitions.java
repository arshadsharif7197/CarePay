package com.carecloud.carepay.patient.delegate.model;

import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author pjohnson on 2019-06-27.
 */
public class DelegateTransitions {

    @Expose
    @SerializedName("delegate_action")
    private TransitionDTO action = new TransitionDTO();
    @Expose
    @SerializedName("merge_patient_profiles")
    private TransitionDTO merge = new TransitionDTO();

    public TransitionDTO getAction() {
        return action;
    }

    public void setAction(TransitionDTO action) {
        this.action = action;
    }

    public TransitionDTO getMerge() {
        return merge;
    }

    public void setMerge(TransitionDTO merge) {
        this.merge = merge;
    }
}
