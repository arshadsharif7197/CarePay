package com.carecloud.carepay.patient.delegate;

import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author pjohnson on 2019-06-27.
 */
class DelegateTransitions {

    @Expose
    @SerializedName("delegate_action")
    private TransitionDTO action = new TransitionDTO();

    public TransitionDTO getAction() {
        return action;
    }

    public void setAction(TransitionDTO action) {
        this.action = action;
    }
}
