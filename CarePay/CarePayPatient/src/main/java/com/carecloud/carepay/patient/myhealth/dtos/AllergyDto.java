package com.carecloud.carepay.patient.myhealth.dtos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pjohnson on 18/07/17.
 */

public class AllergyDto {

    @Expose
    @SerializedName("id")
    private Integer id;

    @Expose
    @SerializedName("name")
    private String name;

    @Expose
    @SerializedName("status")
    private String status;

    @Expose
    @SerializedName("onset_at")
    private String onsetAt;

    @Expose
    @SerializedName("patient_id")
    private Integer patientId;

    @Expose
    @SerializedName("reactions")
    private List<ReactionDto> reactions = new ArrayList<>();

    @Expose
    @SerializedName("practice")
    private String practice;

    @Expose
    @SerializedName("business_entity")
    private BusinessEntity businessEntity;

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

    public String getOnsetAt() {
        return onsetAt;
    }

    public void setOnsetAt(String onsetAt) {
        this.onsetAt = onsetAt;
    }

    public Integer getPatientId() {
        return patientId;
    }

    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    public List<ReactionDto> getReactions() {
        return reactions;
    }

    public void setReactions(List<ReactionDto> reactions) {
        this.reactions = reactions;
    }

    public String getPractice() {
        return practice;
    }

    public void setPractice(String practice) {
        this.practice = practice;
    }

    public BusinessEntity getBusinessEntity() {
        return businessEntity;
    }

    public void setBusinessEntity(BusinessEntity businessEntity) {
        this.businessEntity = businessEntity;
    }
}
