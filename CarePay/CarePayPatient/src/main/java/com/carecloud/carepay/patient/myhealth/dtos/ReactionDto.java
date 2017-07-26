package com.carecloud.carepay.patient.myhealth.dtos;

/**
 * @author pjohnson on 18/07/17.
 */

public class ReactionDto {

    private String name;
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
