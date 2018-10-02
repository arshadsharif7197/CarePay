package com.carecloud.carepay.patient.patientsplash.dtos;

/**
 * Created by Rahul on 11/7/16.
 */

import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LinksDTO {

    @SerializedName("self")
    @Expose
    private SelfLinkDTO self = new SelfLinkDTO();
    @SerializedName("check_latest_version")
    @Expose
    private TransitionDTO checkLatestVersion = new TransitionDTO();

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

    public TransitionDTO getCheckLatestVersion() {
        return checkLatestVersion;
    }

    public void setCheckLatestVersion(TransitionDTO checkLatestVersion) {
        this.checkLatestVersion = checkLatestVersion;
    }
}