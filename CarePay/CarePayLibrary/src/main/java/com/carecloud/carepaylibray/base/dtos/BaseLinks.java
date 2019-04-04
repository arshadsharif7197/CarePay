package com.carecloud.carepaylibray.base.dtos;

import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author pjohnson on 18/07/17.
 */

public class BaseLinks {

    @SerializedName("self")
    @Expose
    private TransitionDTO self = new TransitionDTO();

    public TransitionDTO getSelf() {
        return self;
    }

    /**
     * @param self The self
     */
    public void setSelf(TransitionDTO self) {
        this.self = self;
    }
}
