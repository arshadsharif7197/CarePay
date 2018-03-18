package com.carecloud.carepay.patient.patientsplash.dtos;

/**
 * Created by Rahul on 11/7/16.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LinksDTO {

    @SerializedName("self")
    @Expose
    private SelfLinkDTO self = new SelfLinkDTO();

    /**
     * @return The self
     */
    public SelfLinkDTO getSelf() {
        return self;
    }

    /**
     * @param self The self
     */
    public void setSelf(SelfLinkDTO self) {
        this.self = self;
    }
}