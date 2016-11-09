package com.carecloud.carepay.practice.library.patientmode.dtos;

/**
 * Created by Rahul on 10/27/16.
 */

import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PatientModeTransitionsDTO {

    @SerializedName("start")
    @Expose
    private TransitionDTO start;

    /**
     * @return The start
     */
    public TransitionDTO getStart() {
        return start;
    }

    /**
     * @param start The start
     */
    public void setStart(TransitionDTO start) {
        this.start = start;
    }
}