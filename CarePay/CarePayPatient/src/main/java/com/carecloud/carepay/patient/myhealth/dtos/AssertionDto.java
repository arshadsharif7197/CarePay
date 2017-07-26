package com.carecloud.carepay.patient.myhealth.dtos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author pjohnson on 18/07/17.
 */

public class AssertionDto {

    @Expose
    private Integer id;
    @Expose
    private String name;
    @Expose
    private String status;
    @Expose
    @SerializedName("created_at")
    private String createdAt;
    @Expose
    @SerializedName("patient_id")
    private Integer patientId;
    @Expose
    @SerializedName("assertion_type")
    private String assertionType;
    @Expose
    private String practice;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getPatientId() {
        return patientId;
    }

    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    public String getAssertionType() {
        return assertionType;
    }

    public void setAssertionType(String assertionType) {
        this.assertionType = assertionType;
    }

    public String getPractice() {
        return practice;
    }

    public void setPractice(String practice) {
        this.practice = practice;
    }
}
