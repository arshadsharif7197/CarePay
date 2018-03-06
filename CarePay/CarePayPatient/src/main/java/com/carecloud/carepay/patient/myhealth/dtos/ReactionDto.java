package com.carecloud.carepay.patient.myhealth.dtos;

import com.google.gson.annotations.SerializedName;

/**
 * @author pjohnson on 18/07/17.
 */

public class ReactionDto {

    @SerializedName("name")
    private String name;

    @SerializedName("severity")
    private String severity;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }
}
