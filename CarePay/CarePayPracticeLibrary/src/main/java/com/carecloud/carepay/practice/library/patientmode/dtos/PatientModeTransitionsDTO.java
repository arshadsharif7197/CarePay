package com.carecloud.carepay.practice.library.patientmode.dtos;

/**
 * Created by Rahul on 10/27/16.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PatientModeTransitionsDTO {

    @SerializedName("start")
    @Expose
    private PatientModesStartDTO start;

    /**
     * @return The start
     */
    public PatientModesStartDTO getStart() {
        return start;
    }

    /**
     * @param start The start
     */
    public void setStart(PatientModesStartDTO start) {
        this.start = start;
    }

}