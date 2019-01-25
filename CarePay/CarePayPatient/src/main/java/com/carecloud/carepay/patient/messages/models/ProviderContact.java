package com.carecloud.carepay.patient.messages.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 7/14/17
 */

public class ProviderContact {

    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("patient_id")
    private String patientId;

    @SerializedName("photo")
    private String photo;

    @SerializedName("patient_guid")
    private String patientGuid;

    @SerializedName("primary_specialty")
    private String primarySpecialty;

    @SerializedName("business_entity_id")
    private String businessEntityId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getPatientGuid() {
        return patientGuid;
    }

    public void setPatientGuid(String patientGuid) {
        this.patientGuid = patientGuid;
    }

    public String getPrimarySpecialty() {
        return primarySpecialty;
    }

    public void setPrimarySpecialty(String primarySpecialty) {
        this.primarySpecialty = primarySpecialty;
    }

    public String getBusinessEntityId() {
        return businessEntityId;
    }

    public void setBusinessEntityId(String businessEntityId) {
        this.businessEntityId = businessEntityId;
    }
}
