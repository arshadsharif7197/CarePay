package com.carecloud.carepaylibray.medications.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 2/15/17.
 */

public class MedicationAllergiesLinksDTO {

    @SerializedName("self")
    @Expose
    private MedicationAllergiesLinkDTO self = new MedicationAllergiesLinkDTO();

    @SerializedName("medications")
    @Expose
    private MedicationAllergiesLinkDTO medications = new MedicationAllergiesLinkDTO();

    @SerializedName("search_medications")
    @Expose
    private MedicationAllergiesLinkDTO search = new MedicationAllergiesLinkDTO();

    public MedicationAllergiesLinkDTO getSelf() {
        return self;
    }

    public void setSelf(MedicationAllergiesLinkDTO self) {
        this.self = self;
    }

    public MedicationAllergiesLinkDTO getMedications() {
        return medications;
    }

    public void setMedications(MedicationAllergiesLinkDTO medications) {
        this.medications = medications;
    }

    public MedicationAllergiesLinkDTO getSearch() {
        return search;
    }

    public void setSearch(MedicationAllergiesLinkDTO search) {
        this.search = search;
    }
}
