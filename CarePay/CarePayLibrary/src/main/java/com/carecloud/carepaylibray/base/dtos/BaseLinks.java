package com.carecloud.carepaylibray.base.dtos;

import com.carecloud.carepaylibray.appointments.models.LinkDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author pjohnson on 18/07/17.
 */

public class BaseLinks {

    @SerializedName("self")
    @Expose
    private LinkDTO self = new LinkDTO();

    public LinkDTO getSelf() {
        return self;
    }

    /**
     * @param self The self
     */
    public void setSelf(LinkDTO self) {
        this.self = self;
    }
}
