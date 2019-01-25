package com.carecloud.carepay.patient.messages.models;

import com.google.gson.annotations.SerializedName;

public class AttachmentPostModel {

    @SerializedName("nodeid")
    private String nodeId;

    @SerializedName("description")
    private String description;

    @SerializedName("format")
    private String format;

    @SerializedName("practice_id")
    private String practiceId;

    @SerializedName("patient_id")
    private String patientId;

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getPracticeId() {
        return practiceId;
    }

    public void setPracticeId(String practiceId) {
        this.practiceId = practiceId;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }
}
