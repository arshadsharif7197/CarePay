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
}
