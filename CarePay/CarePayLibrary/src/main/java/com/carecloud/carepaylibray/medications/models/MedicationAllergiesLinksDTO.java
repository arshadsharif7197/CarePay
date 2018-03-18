package com.carecloud.carepaylibray.medications.models;

import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepaylibray.base.dtos.LinkDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 2/15/17.
 */

public class MedicationAllergiesLinksDTO {

    @SerializedName("self")
    @Expose
    private LinkDTO self = new LinkDTO();

    @SerializedName("medications")
    @Expose
    private LinkDTO medications = new LinkDTO();

    @SerializedName("allergies")
    @Expose
    private LinkDTO allergies = new LinkDTO();

    @SerializedName("search_medications")
    @Expose
    private TransitionDTO searchMedications = new TransitionDTO();

    @SerializedName("search_allergies")
    @Expose
    private TransitionDTO searchAllergies = new TransitionDTO();

    public LinkDTO getSelf() {
        return self;
    }

    public void setSelf(LinkDTO self) {
        this.self = self;
    }

    public LinkDTO getMedications() {
        return medications;
    }

    public void setMedications(LinkDTO medications) {
        this.medications = medications;
    }

    public TransitionDTO getSearchMedications() {
        return searchMedications;
    }

    public void setSearchMedications(TransitionDTO searchMedications) {
        this.searchMedications = searchMedications;
    }

    public LinkDTO getAllergies() {
        return allergies;
    }

    public void setAllergies(LinkDTO allergies) {
        this.allergies = allergies;
    }

    public TransitionDTO getSearchAllergies() {
        return searchAllergies;
    }

    public void setSearchAllergies(TransitionDTO searchAllergies) {
        this.searchAllergies = searchAllergies;
    }
}
